package com.mif.movieInsideForum.Module.ChatBot;

import com.mif.movieInsideForum.Module.Ai.MovieSearchService;
import com.mif.movieInsideForum.Module.ChatBot.entity.ChatHistory;
import com.mif.movieInsideForum.Module.ChatBot.repository.ChatHistoryRepository;
import com.mif.movieInsideForum.Module.Movie.dto.MovieResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAIService {

    private final OpenAiChatModel chatModel;
    private final MovieSearchService movieSearchService;
    private final ChatHistoryRepository chatHistoryRepository;

    private static final String SYSTEM_PROMPT = """
        Bạn là một trợ lý AI chuyên phân tích câu hỏi về phim. Hãy phân tích câu hỏi và trả về theo format chuẩn sau:

        Phim [tên phim nếu có].
        Nội dung: [mô tả nếu có].
        Đạo diễn bởi [đạo diễn nếu có].
        Với sự tham gia của các diễn viên [diễn viên nếu có].
        Thuộc thể loại [thể loại nếu có].
        Phát hành năm [năm nếu có].
        Quốc gia: [quốc gia nếu có].
        Thời lượng: [thời lượng] phút.
        Loại phim: [SINGLE/SERIES].
        Số tập: [số tập nếu là series].
        """;

    public String extractSemanticMeaning(String userInput) {
        Prompt prompt = new Prompt(List.of(
            new SystemMessage(SYSTEM_PROMPT),
            new UserMessage(userInput)
        ));

        try {
            ChatResponse response = chatModel.call(prompt);
            return response.getResult().getOutput().getText();
        } catch (Exception e) {
            log.warn("Failed to parse semantic meaning from input", e);
            return userInput;
        }
    }

    public String chatWithOpenAI(String userMessage) {
        Prompt prompt = new Prompt(List.of(new UserMessage(userMessage)));
        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getText();
    }

    @Tool(name = "searchMoviesWithOpenAI", description = "Tìm kiếm phim")
    public Map<String, Object> searchMoviesWithOpenAI(String query, String userId) {
        List<MovieResponseDTO> results = movieSearchService.searchMovies(query);
        log.info("Direct Results: {}", results);

        if (results == null || results.isEmpty()) {
            String semanticQuery = extractSemanticMeaning(query);
            log.info("Semantic Query: {}", semanticQuery);
            List<MovieResponseDTO> semanticResults = movieSearchService.searchMovies(semanticQuery);

            if (semanticResults != null && !semanticResults.isEmpty()) {
                results = semanticResults;
            }
        }

        String promptText = results != null && !results.isEmpty()
            ? buildMoviePrompt(query, results)
            : "Tôi muốn hỏi về: " + query;

        String response = chatWithOpenAI(promptText);
        chatHistoryRepository.save(new ChatHistory(userId, query, response, results));

        return Map.of(
            "response", response,
            "movies", results,
            "semanticQuery", query
        );
    }

    public Slice<ChatHistory> getChatHistory(String userId, Pageable pageable) {
        return chatHistoryRepository.findByUserIdOrderByTimestampDesc(userId, pageable);
    }

    public void clearChatHistory(String userId) {
        chatHistoryRepository.deleteByUserId(userId);
    }

    private String buildMoviePrompt(String query, List<MovieResponseDTO> movies) {
        StringBuilder sb = new StringBuilder("Dưới đây là dữ liệu phim từ hệ thống của tôi:\n\n");
        for (MovieResponseDTO movie : movies) {
            sb.append("=== THÔNG TIN PHIM ===\n")
              .append("Tên phim: ").append(movie.getTitle()).append("\n")
              .append("Năm phát hành: ").append(movie.getReleaseDate()).append("\n");

            if (movie.getDescription() != null)
                sb.append("Mô tả: ").append(movie.getDescription()).append("\n");
            if (movie.getGenre() != null && !movie.getGenre().isEmpty())
                sb.append("Thể loại: ")
                  .append(movie.getGenre().stream().map(g -> g.getCategoryName()).collect(Collectors.joining(", ")))
                  .append("\n");
            if (movie.getDirector() != null && !movie.getDirector().isEmpty())
                sb.append("Đạo diễn: ")
                  .append(movie.getDirector().stream().map(d -> d.getName()).collect(Collectors.joining(", ")))
                  .append("\n");
            if (movie.getCountry() != null)
                sb.append("Quốc gia: ").append(movie.getCountry()).append("\n");
            if (movie.getDuration() != null)
                sb.append("Thời lượng: ").append(movie.getDuration()).append(" phút\n");
            if (movie.getMovieType() != null) {
                sb.append("Loại phim: ").append(movie.getMovieType()).append("\n");
                if ("SERIES".equals(movie.getMovieType()) && movie.getTotalEpisodes() != null)
                    sb.append("Số tập: ").append(movie.getTotalEpisodes()).append("\n");
            }
            if (movie.getAwards() != null && !movie.getAwards().isEmpty()) {
                sb.append("Giải thưởng: ")
                  .append(movie.getAwards().stream()
                          .map(a -> a.getName() + " (" + a.getDate() + ")")
                          .collect(Collectors.joining(", ")))
                  .append("\n");
            }
            sb.append("\n");
        }
        sb.append("Dựa trên thông tin phim trên, hãy trả lời câu hỏi: ").append(query);
        return sb.toString();
    }

}
