package com.mif.movieInsideForum.Module.Chat;

import com.mif.movieInsideForum.Collection.ChatMessage;
import com.mif.movieInsideForum.Collection.GroupChat;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final GroupChatRepository groupChatRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public ChatMessage saveMessage(ChatMessage message) {
        GroupChat DBgroupChat = groupChatRepository.findById(message.getGroupId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy group chat với id: " + message.getGroupId()));
        DBgroupChat.setNewestMessage(message.getContent());
        groupChatRepository.save(DBgroupChat);
        return chatMessageRepository.save(message);
    }

    @Override
    public Slice<ChatMessage> getMessagesByGroupId(ObjectId groupId, Pageable pageable) {
        return chatMessageRepository.findByGroupId(groupId, pageable);
    }
}