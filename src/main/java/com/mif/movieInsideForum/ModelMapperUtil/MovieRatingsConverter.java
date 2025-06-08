package com.mif.movieInsideForum.ModelMapperUtil;

import com.mif.movieInsideForum.Module.MovieRating.Entity.MovieRatings;
import com.mif.movieInsideForum.DTO.MovieRatingsRequestDTO;
import com.mif.movieInsideForum.Module.Movie.MovieSentimentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MovieRatingsConverter {
    private final MovieSentimentRepository sentimentRepository;

    public MovieRatings convertToEntity(MovieRatingsRequestDTO dto) {
        MovieRatings entity = new MovieRatings();
        entity.setMovieId(dto.getMovieId());
        entity.setRatingValue(dto.getRatingValue());
        entity.setComment(dto.getComment());
        return entity;
    }

//    public MovieRatingsResponseDTO convertToDTO(MovieRatings entity) {
//        MovieRatingsResponseDTO dto = new MovieRatingsResponseDTO();
//        dto.setId(entity.getId());
//        dto.setMovieId(entity.getMovieId());
//        dto.setUserId(entity.getUser().getId());
//        dto.setRatingValue(entity.getRatingValue());
//        dto.setComment(entity.getComment());
//        dto.setCreatedAt(entity.getCreatedAt());
//
//        // Get sentiment information if available
//        if (entity.getId() != null) {
//            MovieSentiment sentiment = sentimentRepository.findByCommentId(entity.getId().toString())
//                    .orElse(null);
//            if (sentiment != null) {
//                dto.setSentiment(sentiment.getSentiment());
//                dto.setPositiveScore(sentiment.getPositiveScore());
//                dto.setNegativeScore(sentiment.getNegativeScore());
//                dto.setNeutralScore(sentiment.getNeutralScore());
//                dto.setMixedScore(sentiment.getMixedScore());
//            }
//        }
//
//        return dto;
//    }
}