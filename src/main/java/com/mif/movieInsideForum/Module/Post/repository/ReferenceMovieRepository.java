package com.mif.movieInsideForum.Module.Post.repository;

import com.mif.movieInsideForum.Module.Post.entity.ReferenceMovie;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReferenceMovieRepository extends MongoRepository<ReferenceMovie, ObjectId> {
    Optional<ReferenceMovie> findByMovieIdAndGroupId(ObjectId movieId, ObjectId groupId);
    
    @Query(value = "{ 'groupId': ?0 }", sort = "{ 'referenceCount': -1 }")
    java.util.List<ReferenceMovie> findTopMoviesByGroup(ObjectId groupId);
} 