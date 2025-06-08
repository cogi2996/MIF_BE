package com.mif.movieInsideForum.Module.Chat;

import com.mif.movieInsideForum.Collection.ChatMessage;
import com.mif.movieInsideForum.Collection.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;


@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat.sendMessage")
    public ChatMessage sendMessage(@Payload ChatMessage message, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        message.setSenderName(currentUser.getDisplayName());
        message.setAvatar(currentUser.getProfilePictureUrl());
        message.setSenderId(currentUser.getId());
        // cần một service check available chat ( id nhóm hợp lệ  && phải trong nhóm )
        chatMessageService.saveMessage(message);
        messagingTemplate.convertAndSend("/topic/group/" + message.getGroupId(), message);
        log.info("Message_sent: " + message.getContent());
        return message;
    }





}
