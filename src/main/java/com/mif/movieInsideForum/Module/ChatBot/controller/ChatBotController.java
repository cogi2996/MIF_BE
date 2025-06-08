package com.mif.movieInsideForum.Module.ChatBot.controller;

import com.mif.movieInsideForum.DTO.ResponseWrapper;
import com.mif.movieInsideForum.Module.ChatBot.OpenAIService;
import com.mif.movieInsideForum.Module.ChatBot.entity.ChatHistory;
import com.mif.movieInsideForum.Module.ChatBot.service.ChatBotService;
import com.mif.movieInsideForum.Security.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class ChatBotController {
    private final ChatBotService chatBotService;
    private final AuthenticationFacade authenticationFacade;
    private final OpenAIService openAIService;
    
    @PostMapping("/chat")
    public ResponseEntity<ResponseWrapper<Object>> chat(@RequestBody Map<String,String> body) {
        try {
            Object response = chatBotService.processUserInput(body.get("message"));
            
            // Kiểm tra nếu response là Map (có type và data)
            if (response instanceof Map) {
                Map<String, Object> responseMap = (Map<String, Object>) response;
                String type = String.valueOf(responseMap.get("type"));
                Object data = responseMap.get("data");
                
                return ResponseEntity.ok(ResponseWrapper.<Object>builder()
                    .status("success")
                    .message("Xử lý yêu cầu thành công")
                    .data(data)
                    .type(type)
                    .build());
            }
            
            // Nếu response là String (chitchat hoặc error)
            return ResponseEntity.ok(ResponseWrapper.<Object>builder()
                .status("success")
                .message("Xử lý yêu cầu thành công")
                .data(response)
                .build());
                
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseWrapper.<Object>builder()
                .status("error")
                .message("Có lỗi xảy ra: " + e.getMessage())
                .data(null)
                .type("error")
                .build());
        }
    }

    @GetMapping("/history")
    public ResponseEntity<ResponseWrapper<Slice<ChatHistory>>> getChatHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            String userId = authenticationFacade.getUser().getId().toString();
            Pageable pageable = PageRequest.of(page, size);
            Slice<ChatHistory> history = openAIService.getChatHistory(userId, pageable);
            
            return ResponseEntity.ok(ResponseWrapper.<Slice<ChatHistory>>builder()
                .status("success")
                .data(history)
                .message("Lấy lịch sử chat thành công")
                .type("history")
                .build());
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseWrapper.<Slice<ChatHistory>>builder()
                .status("error")
                .message("Có lỗi xảy ra: " + e.getMessage())
                .data(null)
                .type("error")
                .build());
        }
    }

    @DeleteMapping("/history")
    public ResponseEntity<ResponseWrapper<Void>> clearChatHistory() {
        try {
            String userId = authenticationFacade.getUser().getId().toString();
            openAIService.clearChatHistory(userId);
            
            return ResponseEntity.ok(ResponseWrapper.<Void>builder()
                .status("success")
                .message("Xóa lịch sử chat thành công")
                .type("clear_history")
                .build());
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseWrapper.<Void>builder()
                .status("error")
                .message("Có lỗi xảy ra: " + e.getMessage())
                .data(null)
                .type("error")
                .build());
        }
    }       
} 