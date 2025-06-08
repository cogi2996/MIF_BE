package com.mif.movieInsideForum.Module.Post.repository;

import com.mif.movieInsideForum.Collection.Field.ReportStatus;
import com.mif.movieInsideForum.Module.Post.entity.ReportPost;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportPostRepository extends MongoRepository<ReportPost, ObjectId> {
    Optional<ReportPost> findByPostId(String postId);
    
    Page<ReportPost> findByGroupIdAndStatus(String groupId, ReportStatus status, Pageable pageable);
} 