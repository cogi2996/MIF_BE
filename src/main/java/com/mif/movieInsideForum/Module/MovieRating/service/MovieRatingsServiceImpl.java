package com.mif.movieInsideForum.Module.MovieRating.service;

import com.mif.movieInsideForum.Collection.Field.Ratings;
import com.mif.movieInsideForum.Collection.User;
import com.mif.movieInsideForum.DTO.MovieRatingsRequestDTO;
import com.mif.movieInsideForum.DTO.MovieRatingsResponseDTO;
import com.mif.movieInsideForum.Mapper.MovieRatingsMapper;
import com.mif.movieInsideForum.Module.SentimentAnalysis.SentimentAnalysisService;
import com.mif.movieInsideForum.Module.MovieRating.dto.MovieSentimentStatsDTO;
import com.mif.movieInsideForum.Module.MovieRating.Entity.MovieRatings;
import com.mif.movieInsideForum.Module.MovieRating.repository.MovieRatingsRepository;
import com.mif.movieInsideForum.Module.MovieRating.dto.MovieRatingEventDTO;
import com.mif.movieInsideForum.Queue.MovieRatingQueueDefine;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.mif.movieInsideForum.Module.MovieRating.dto.MovieRatingMonthCount;

@Service
@RequiredArgsConstructor
public class MovieRatingsServiceImpl implements MovieRatingsService {
    private final MovieRatingsRepository movieRatingsRepository;
    private final MovieRatingsMapper movieRatingsMapper;
    private final SentimentAnalysisService sentimentAnalysisService;
    private final MovieSentimentStatsService movieSentimentStatsService;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional
    public MovieRatingsResponseDTO rateMovie(ObjectId userId, MovieRatingsRequestDTO movieRatingsRequestDTO) {
        // Kiểm tra giá trị đánh giá
        if (movieRatingsRequestDTO.getRatingValue() < 0 || movieRatingsRequestDTO.getRatingValue() > 10) {
            throw new IllegalArgumentException("Giá trị đánh giá phải nằm trong khoảng từ 0 đến 10");
        }

        // Kiểm tra xem người dùng đã đánh giá bộ phim này chưa
        Optional<MovieRatings> existingRating = movieRatingsRepository.findByUserIdAndMovieId(userId, movieRatingsRequestDTO.getMovieId());

        MovieRatings movieRatings;
        if (existingRating.isPresent()) {
            // Nếu đã có đánh giá, cập nhật giá trị và bình luận
            movieRatings = existingRating.get();
            movieRatings.setRatingValue(movieRatingsRequestDTO.getRatingValue());
            movieRatings.setComment(movieRatingsRequestDTO.getComment());
        } else {
            // Nếu chưa có đánh giá, tạo mới
            movieRatings = movieRatingsMapper.toEntity(movieRatingsRequestDTO);
            movieRatings.setCreatedAt(new Date());
            movieRatings.setUser(User.builder().id(userId).build());
        }

        // Phân tích sentiment nếu có comment
        if (movieRatings.getComment() != null && !movieRatings.getComment().trim().isEmpty()) {
            sentimentAnalysisService.analyzeSentiment(movieRatings);
        }

        // Lưu đánh giá
        MovieRatings savedMovieRatings = movieRatingsRepository.save(movieRatings);

        // Send event to update sentiment stats
        if (savedMovieRatings.getSentiment() != null) {
            String oldSentiment = existingRating.map(MovieRatings::getSentiment).orElse(null);
            MovieRatingEventDTO event = MovieRatingEventDTO.builder()
                    .movieId(savedMovieRatings.getMovieId())
                    .newSentiment(savedMovieRatings.getSentiment())
                    .oldSentiment(oldSentiment)
                    .isDeleted(false)
                    .build();
            rabbitTemplate.convertAndSend(MovieRatingQueueDefine.EXCHANGE_NAME, MovieRatingQueueDefine.ROUTING_KEY, event);
        }

        // Trả về DTO của đánh giá đã lưu
        return movieRatingsMapper.toDto(savedMovieRatings);
    }

    @Override
    @Transactional
    public void removeRating(ObjectId userId, ObjectId movieId) {
        MovieRatings existingRating = movieRatingsRepository.findByUserIdAndMovieId(userId, movieId)
                .orElseThrow(() -> new RuntimeException("Rating not found"));

        // Send event to decrease sentiment count
        if (existingRating.getSentiment() != null) {
            MovieRatingEventDTO event = MovieRatingEventDTO.builder()
                    .movieId(movieId)
                    .oldSentiment(existingRating.getSentiment())
                    .isDeleted(true)
                    .build();
            rabbitTemplate.convertAndSend(MovieRatingQueueDefine.EXCHANGE_NAME, MovieRatingQueueDefine.ROUTING_KEY, event);
        }

        movieRatingsRepository.deleteByUserIdAndMovieId(userId, movieId);
    }

    @Override
    public Ratings getAverageRating(ObjectId movieId) {
        Ratings ratings = movieRatingsRepository.findAverageRatingByMovieId(movieId);
        if (ratings != null) {
            ratings.setAverageRating(Math.round(ratings.getAverageRating() * 10.0) / 10.0);
        }
        return ratings;
    }

    @Override
    public Slice<MovieRatingsResponseDTO> getAllRatingsByMovieId(ObjectId movieId, Pageable pageable) {
        Optional<Slice<MovieRatings>> movieRatingsOpts = movieRatingsRepository.findAllByMovieId(movieId, pageable);
        return movieRatingsOpts.map(movies -> movies.map(movieRatingsMapper::toDto))
                .orElseGet(() -> new SliceImpl<>(List.of(), pageable, false));
    }

    @Override
    public MovieSentimentStatsDTO getMovieSentimentStats(ObjectId movieId) {
        return movieSentimentStatsService.getMovieSentimentStats(movieId);
    }

    @Override
    public java.util.Map<Integer, Integer> countRatingsByMonth(int year) {
        java.util.List<MovieRatingMonthCount> results = movieRatingsRepository.countRatingsByMonth(year);
        java.util.Map<Integer, Integer> monthCount = new java.util.HashMap<>();
        for (int i = 1; i <= 12; i++) monthCount.put(i, 0);
        for (MovieRatingMonthCount item : results) {
            monthCount.put(item.getMonth(), item.getCount());
        }
        return monthCount;
    }
}
