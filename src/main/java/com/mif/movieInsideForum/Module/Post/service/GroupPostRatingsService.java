package com.mif.movieInsideForum.Module.Post.service;

import com.mif.movieInsideForum.Collection.Field.VoteType;
import org.bson.types.ObjectId;

public interface GroupPostRatingsService {
    void upVote(ObjectId postId, ObjectId userId);
    void downVote(ObjectId postId, ObjectId userId);
    void removeVote(ObjectId postId, ObjectId userId);
    int getCurrentVotes(ObjectId postId);
    VoteType getUserVote(ObjectId postId, ObjectId userId);
}