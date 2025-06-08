package com.mif.movieInsideForum.Module.Chat;

import com.mif.movieInsideForum.Collection.ChatMessage;
import com.mif.movieInsideForum.Collection.GroupChat;
import com.mif.movieInsideForum.DTO.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
//@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatRestController {
    private final UserGroupChatService userGroupChatService;
    private final ChatMessageService chatMessageService;

    @PostMapping("/joined-group-chat")
    public ResponseEntity<ResponseWrapper<Void>> joinGroupChat(Principal principal, @RequestParam ObjectId groupId) {
        ObjectId userId = new ObjectId(principal.getName());
        userGroupChatService.joinGroupChat(userId, groupId);
        return ResponseEntity.ok(ResponseWrapper.<Void>builder()
                .status("success")
                .message("Successfully joined group chat")
                .build());
    }

    @GetMapping("/group-chats")
    public ResponseEntity<ResponseWrapper<Slice<GroupChat>>> getGroupChats(Principal principal, @PageableDefault(size = 10, sort = "updateTime", direction = Sort.Direction.ASC) Pageable pageable) {
        ObjectId userId = new ObjectId(principal.getName());
        Slice<GroupChat> groupChats = userGroupChatService.getGroupChats(userId, pageable);
        return ResponseEntity.ok(ResponseWrapper.<Slice<GroupChat>>builder()
                .status("success")
                .message("Group chats retrieved successfully")
                .data(groupChats)
                .build());
    }

    @GetMapping("/chat/group/{groupId}/messages")
    public ResponseEntity<ResponseWrapper<Slice<ChatMessage>>> getMessagesByGroupId(@PathVariable ObjectId groupId, @PageableDefault(size = 10, sort = "createAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Slice<ChatMessage> messages = chatMessageService.getMessagesByGroupId(groupId, pageable);
        return ResponseEntity.ok(ResponseWrapper.<Slice<ChatMessage>>builder()
                .status("success")
                .message("Messages retrieved successfully")
                .data(messages)
                .build());
    }
}