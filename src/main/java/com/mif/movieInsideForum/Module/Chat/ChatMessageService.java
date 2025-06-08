package com.mif.movieInsideForum.Module.Chat;

import com.mif.movieInsideForum.Collection.ChatMessage;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ChatMessageService {
    ChatMessage saveMessage(ChatMessage message);
    Slice<ChatMessage> getMessagesByGroupId(ObjectId groupId, Pageable pageable);


}
