package com.mif.movieInsideForum.Module.Actor;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.mif.movieInsideForum.Collection.Actor;

public interface ActorRepository extends MongoRepository<Actor, ObjectId> {
    Slice<Actor> findAllByOrderByScoreRankDesc(Pageable pageable);

    // average rating of all actors
    @Aggregation(pipeline = {"{$match: {_id: ?0}}", "{$project: {RatingsAvg: {$avg: '$ratings.ratingValue'}}}"})
    Double getAverageRating(ObjectId actorId);

    // get user rating for an actor
    @Aggregation(pipeline = {
            "{$match: {_id: ?0}}",
            "{$project: {ratingValue: {$arrayElemAt: ['$ratings.ratingValue', {$indexOfArray: ['$ratings.ratedUserId', ?1]}]}}}"
    })
    Double getUserRating(ObjectId actorId, ObjectId userId);

    @Query("{}") // find all with no condition
    Slice<Actor> findAllWithPag(Pageable pageable);

    Slice<Actor> findByNameContainingIgnoreCase(String name, Pageable pageable);


}