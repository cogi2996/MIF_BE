package com.mif.movieInsideForum.Module.Post.repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.mif.movieInsideForum.Collection.SavedPost;

public interface SavedPostRepository extends MongoRepository<SavedPost, ObjectId> {
    Optional<Slice<SavedPost>> findByUserId(ObjectId userId, Pageable pageable);

    boolean existsByUserIdAndPostId(ObjectId userId, ObjectId postId);

    void deleteByUserIdAndPostId(ObjectId userId, ObjectId postId);

    @Query("{ 'user': ?0, 'post': { $in: ?1 } }")
    List<SavedPost> findByUserIdAndPostIdIn(ObjectId userId, List<ObjectId> postIds);

    // delete by groupId
    void deleteByGroupId(ObjectId groupId);

    // delete by postId
    void deleteByPostId(ObjectId postId);



}