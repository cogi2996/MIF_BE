package com.mif.movieInsideForum.Module.MovieRating.service;

import com.mif.movieInsideForum.Collection.Field.Ratings;
import com.mif.movieInsideForum.DTO.MovieRatingsRequestDTO;
import com.mif.movieInsideForum.DTO.MovieRatingsResponseDTO;
import com.mif.movieInsideForum.Module.MovieRating.dto.MovieSentimentStatsDTO;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MovieRatingsService {
    MovieRatingsResponseDTO rateMovie(ObjectId userId, MovieRatingsRequestDTO movieRatingsRequestDTO);
    void removeRating(ObjectId userId, ObjectId movieId);
    Ratings getAverageRating(ObjectId movieId);
    Slice<MovieRatingsResponseDTO> getAllRatingsByMovieId(ObjectId movieId, Pageable pageable);
    MovieSentimentStatsDTO getMovieSentimentStats(ObjectId movieId);
    java.util.Map<Integer, Integer> countRatingsByMonth(int year);
}