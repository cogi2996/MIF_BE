package com.mif.movieInsideForum.Module.ChatBot.repository;

import com.mif.movieInsideForum.Module.ChatBot.entity.ChatHistory;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatHistoryRepository extends MongoRepository<ChatHistory, ObjectId> {
    Slice<ChatHistory> findByUserIdOrderByTimestampDesc(String userId, Pageable pageable);
    void deleteByUserId(String userId);
} 