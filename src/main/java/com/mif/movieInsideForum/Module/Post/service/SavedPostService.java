package com.mif.movieInsideForum.Module.Post.service;

import java.util.List;
import java.util.Map;

import com.mif.movieInsideForum.Module.Post.DTO.GroupPostResponseDTO;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface SavedPostService {
    void savePost(ObjectId userId, ObjectId postId);
    void unsavePost(ObjectId userId, ObjectId postId);
    boolean isPostSaved(ObjectId userId, ObjectId postId);
    Slice<GroupPostResponseDTO> getSavedPosts(ObjectId userId, Pageable pageable);
    Map<String, Boolean> batchCheckSavedStatus(ObjectId userId, List<ObjectId> postIds);
}