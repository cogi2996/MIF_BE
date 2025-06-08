package com.mif.movieInsideForum.Module.ChatBot.service;

import com.mif.movieInsideForum.Module.ChatBot.repository.ChatHistoryRepository;
import com.mif.movieInsideForum.Security.AuthenticationFacade;
import com.mif.movieInsideForum.Module.ChatBot.entity.ChatHistory;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatBotService {
    private final OpenAiChatModel chatModel;
    private final MovieForumTools movieForumTools;
    private final ChatHistoryRepository chatHistoryRepository;
    private final AuthenticationFacade authenticationFacade;
    private static final String SYSTEM_PROMPT = """
        Bạn là trợ lý ảo của hệ thống MovieInsideForum. Nhiệm vụ:
        1. Phân tích yêu cầu của người dùng và sử dụng function calling đã được hệ thống cung cấp
        3. Chỉ trả lời trực tiếp bằng văn bản nếu người dùng muốn trò chuyện thông thường về phim ảnh.
        4. Không tự tạo dữ liệu, luôn dùng công cụ để lấy thông tin.
        5. Nếu xóa lịch sử chat thì sau khi thực thi xong function calling thì trả về chuỗi rỗng.
        6. Nếu function call là search_movie thì nếu có kết quả phim của hệ thống phải thêm đường dẫn dạng **[link](/movies/:movieId)**
        """;

    public Object processUserInput(String userInput) {
        try {
            if (userInput == null || userInput.trim().isEmpty()) {
                return "Vui lòng nhập câu hỏi của bạn";
            }

            // Tạo prompt cho AI với system message và user input
            Prompt prompt = new Prompt(
                List.of(
                    new SystemMessage(SYSTEM_PROMPT),
                    new UserMessage(userInput.trim())
                ),
                OpenAiChatOptions.builder()
                    .temperature(0.2)
                    .toolCallbacks(ToolCallbacks.from(movieForumTools))
                    .internalToolExecutionEnabled(true) 
                    .build()
            );

            // Gọi AI để xử lý
            ChatResponse response = chatModel.call(prompt);
            String responseText = response.getResult().getOutput().getText();
            if (!responseText.equals("")) {
                chatHistoryRepository.save(new ChatHistory(authenticationFacade.getUserId().toString(), userInput, responseText, null));
            }
            return responseText;

        } catch (Exception e) {
            return Map.of(
                "type", "error",
                "data", "Xin lỗi, có lỗi xảy ra khi xử lý yêu cầu của bạn: " + e.getMessage()
            );
        }
    }
} 