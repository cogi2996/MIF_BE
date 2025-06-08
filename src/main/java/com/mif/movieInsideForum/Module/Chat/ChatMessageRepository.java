package com.mif.movieInsideForum.Module.Chat;

import com.mif.movieInsideForum.Collection.ChatMessage;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, ObjectId> {
    Slice<ChatMessage> findByGroupId(ObjectId groupId, Pageable pageable);
}