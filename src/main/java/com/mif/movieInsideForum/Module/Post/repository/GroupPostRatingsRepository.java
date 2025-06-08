package com.mif.movieInsideForum.Module.Post.repository;

import com.mif.movieInsideForum.Collection.GroupPostRatings;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface GroupPostRatingsRepository extends MongoRepository<GroupPostRatings, ObjectId> {
    Optional<GroupPostRatings> findByPostIdAndUserId(ObjectId postId, ObjectId userId);
    void deleteByPostIdAndUserId(ObjectId postId, ObjectId userId);
    List<GroupPostRatings> findByPostId(ObjectId postId);

    @Aggregation(pipeline = {
            "{ '$match': { 'postId': ?0 } }",
            "{ '$group': { '_id': null, 'totalRating': { '$sum': { '$cond': [ { '$eq': [ '$ratings', 'UPVOTE' ] }, 1, -1 ] } } } }"
    })
    Optional<Integer> getTotalRating(ObjectId postId);

    void deleteByGroupId(ObjectId groupId);

    void deleteByPostId(ObjectId postId);
}