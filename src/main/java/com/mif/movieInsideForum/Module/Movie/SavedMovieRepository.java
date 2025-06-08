package com.mif.movieInsideForum.Module.Movie;

import com.mif.movieInsideForum.Collection.SavedMovie;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


public interface SavedMovieRepository extends MongoRepository<SavedMovie, ObjectId> {
     Optional< Slice<SavedMovie>> findByUserId(ObjectId userId, Pageable pageable);

    boolean existsByUserIdAndMovieId(ObjectId userId, ObjectId movieId);

    void deleteByUserIdAndMovieId(ObjectId userId, ObjectId movieId);

    @Query("{ 'user': ?0, 'movie': { $in: ?1 } }")
    List<SavedMovie> findByUserIdAndMovieIdIn(ObjectId userId, List<ObjectId> movieIds);

    void deleteByMovieId(ObjectId movieId);

}