// package com.mif.movieInsideForum.Module.Ai;

// import com.mif.movieInsideForum.Module.Movie.dto.MovieResponseDTO;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.ai.chat.client.ChatClient;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.stream.Collectors;

// @Slf4j
// @RestController
// @RequestMapping("/ai")
// public class AIChatController {

//     private final ChatClient chatClient;
//     private final MovieSearchService movieSearchService;

//     public AIChatController(ChatClient.Builder chatClientBuilder, MovieSearchService movieSearchService) {
//         this.chatClient = chatClientBuilder.build();
//         this.movieSearchService = movieSearchService;
//     }

//     /**
//      * API chat với trợ lý phim ảnh sử dụng RAG
//      * @param request Yêu cầu chat từ người dùng
//      * @return Phản hồi chat và thông tin phim liên quan
//      */
//     @PostMapping("/ask")
//     public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, String> request) {
//         try {
//             String userMessage = request.get("message");
//             if (userMessage == null || userMessage.trim().isEmpty()) {
//                 return ResponseEntity.badRequest().body(Map.of("error", "Tin nhắn không được để trống"));
//             }
            
//             log.info("Đang xử lý yêu cầu chat: {}", userMessage);
            
//             // 1. Tìm kiếm phim liên quan bằng vector search
//             List<MovieResponseDTO> relevantMovies = movieSearchService.searchMovies(userMessage);
//             log.info("Tìm thấy {} phim liên quan", relevantMovies.size());
            
//             // 2. Tạo context từ thông tin phim
//             String context = buildMovieContext(relevantMovies);
            
//             // 3. Tạo system prompt với context và user message
//             String systemPrompt = buildSystemPrompt(context, userMessage);
            
//             // 4. Gọi API chat với context
//             String response = chatClient.prompt()
//                     .system(systemPrompt)
//                     .user(userMessage)
//                     .call()
//                     .content();
            
//             // 5. Trả về kết quả
//             Map<String, Object> result = new HashMap<>();
//             result.put("response", response);
//             result.put("movies", relevantMovies);
            
//             return ResponseEntity.ok(result);
            
//         } catch (Exception e) {
//             log.error("Lỗi xử lý chat: {}", e.getMessage(), e);
//             return ResponseEntity.badRequest().body(Map.of("error", "Lỗi khi xử lý chat: " + e.getMessage()));
//         }
//     }

//     private String buildMovieContext(List<MovieResponseDTO> movies) {
//         if (movies.isEmpty()) {
//             return "Hiện tại không có thông tin về phim liên quan trong hệ thống của chúng tôi.";
//         }

//         return movies.stream()
//                 .map(movie -> {
//                     StringBuilder info = new StringBuilder();
//                     info.append("=== THÔNG TIN PHIM ===\n");
//                     info.append("Tên phim: ").append(movie.getTitle()).append("\n");
//                     info.append("Mô tả: ").append(movie.getDescription()).append("\n");
//                     info.append("Năm phát hành: ").append(movie.getReleaseDate() != null ? movie.getReleaseDate().toString() : "Không rõ").append("\n");
//                     info.append("Quốc gia: ").append(movie.getCountry() != null ? movie.getCountry() : "Không rõ").append("\n");
//                     info.append("Thời lượng: ").append(movie.getDuration() != null ? movie.getDuration() + " phút" : "Không rõ").append("\n");
//                     info.append("Loại phim: ").append(movie.getMovieType() != null ? movie.getMovieType() : "Không rõ").append("\n");
                    
//                     if (movie.getMovieType() != null && movie.getMovieType().equals("SERIES") && movie.getTotalEpisodes() != null) {
//                         info.append("Số tập: ").append(movie.getTotalEpisodes()).append("\n");
//                     }
                    
//                     if (movie.getDirector() != null && !movie.getDirector().isEmpty()) {
//                         info.append("Đạo diễn: ").append(movie.getDirector().stream()
//                                 .map(d -> d.getName())
//                                 .collect(Collectors.joining(", ")))
//                                 .append("\n");
//                     }

//                     if (movie.getGenre() != null && !movie.getGenre().isEmpty()) {
//                         info.append("Thể loại: ").append(movie.getGenre().stream()
//                                 .map(g -> g.getCategoryName())
//                                 .collect(Collectors.joining(", ")))
//                                 .append("\n");
//                     }

//                     if (movie.getAwards() != null && !movie.getAwards().isEmpty()) {
//                         info.append("Giải thưởng: ").append(movie.getAwards().stream()
//                                 .map(a -> a.getName() + " (" + a.getDate() + ")")
//                                 .collect(Collectors.joining(", ")))
//                                 .append("\n");
//                     }
                    
//                     return info.toString();
//                 })
//                 .collect(Collectors.joining("\n\n"));
//     }

//     private String buildSystemPrompt(String context, String userMessage) {
//         return String.format(
//             "Bạn là một trợ lý ảo chuyên sâu về lĩnh vực phim ảnh.\n\n" +
//             "Người dùng đang hỏi về: \"%s\"\n\n" +
//             "Dưới đây là danh sách các phim hiện có trong hệ thống:\n%s\n\n" +
//             "HƯỚNG DẪN TRẢ LỜI:\n" +
//             "1. BẮT BUỘC: Chỉ trả lời dựa trên danh sách phim được đính kèm ở trên.\n" +
//             "2. Nếu có phim phù hợp với câu hỏi:\n" +
//             "   - Trình bày chi tiết về phim đó (tên, nội dung, thể loại, đạo diễn, giải thưởng, v.v.).\n" +
//             "   - KHÔNG được nhắc đến bất kỳ phim nào không có trong danh sách trên.\n" +
//             "3. Nếu không có phim phù hợp:\n" +
//             "   - Trả lời rõ ràng: \"Hiện tại không có phim phù hợp trong hệ thống của chúng tôi.\"\n" +
//             "   - Có thể đề xuất phim tương tự dựa trên kiến thức chung (nếu cần).\n" +
//             "4. KHÔNG tự ý thêm thông tin không có trong dữ liệu đính kèm.\n" +
//             "5. Nếu tên phim trong danh sách trùng khớp với từ khóa người dùng (không phân biệt chữ hoa, chữ thường hay dấu), hãy ưu tiên trả lời về phim đó.\n",
//             userMessage, context
//         );
//     }
    
// }