package com.mif.movieInsideForum.Module.Comment;

import com.mif.movieInsideForum.Collection.Comment;
import com.mif.movieInsideForum.Collection.User;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CommentService {
    Comment createComment(Comment comment, User user);
    Slice<Comment> getCommentsByPostId(ObjectId postId, Pageable pageable);
    Comment upvote(ObjectId commentId, ObjectId userId);
    Comment downvote(ObjectId commentId, ObjectId userId);
    // Comment removeVote(ObjectId commentId, ObjectId userId);
}
