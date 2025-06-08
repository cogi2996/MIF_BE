package com.mif.movieInsideForum.Module.MovieRating.service;

import com.mif.movieInsideForum.Module.MovieRating.dto.MovieRatingEventDTO;
import com.mif.movieInsideForum.Module.MovieRating.dto.MovieSentimentStatsDTO;
import org.bson.types.ObjectId;

public interface MovieSentimentStatsService {
    void updateSentimentStats(MovieRatingEventDTO event);
    MovieSentimentStatsDTO getMovieSentimentStats(ObjectId movieId);
} 