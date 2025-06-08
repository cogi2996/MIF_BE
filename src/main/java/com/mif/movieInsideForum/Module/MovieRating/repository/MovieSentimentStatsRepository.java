package com.mif.movieInsideForum.Module.MovieRating.repository;

import com.mif.movieInsideForum.Module.MovieRating.Entity.MovieSentimentStats;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MovieSentimentStatsRepository extends MongoRepository<MovieSentimentStats, ObjectId> {
    Optional<MovieSentimentStats> findByMovieId(ObjectId movieId);
} 