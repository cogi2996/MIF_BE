package com.mif.movieInsideForum.Module.Movie;

import com.mif.movieInsideForum.Collection.MovieSentiment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieSentimentRepository extends MongoRepository<MovieSentiment, String> {
    List<MovieSentiment> findByMovieId(String movieId);
    List<MovieSentiment> findByCommentId(String commentId);
}