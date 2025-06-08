package com.mif.movieInsideForum.Module.Comment;

import com.mif.movieInsideForum.Collection.Comment;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface CommentRepository extends MongoRepository<Comment, ObjectId> {
    @Query("{postId: ?0}")
    Slice<Comment> getCommentsByPostId(ObjectId postId, Pageable pageable);

    // delete all by groupId
    void deleteByGroupId(ObjectId postId);

    // delete all by postId
    void deleteByPostId(ObjectId postId);
}