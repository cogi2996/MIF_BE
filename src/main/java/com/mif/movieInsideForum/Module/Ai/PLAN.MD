quy trình chuẩn RAG (Retrieval-Augmented Generation) dùng Mongo + Ollama trong Spring Boot cho chatbot phim của bro luôn nha 👇

🟣 1. Bro đã cài:
✅ Ollama (llama3.2:latest)

✅ nomic-embed-text:latest (embedding model)

✅ MongoDB với collection films chứa data

👉 Quá ổn! Chúng ta chỉ việc ráp RAG flow thôi.

🚀 2. Flow chuẩn RAG để chatbot phim
Step Mô tả
🟡 Người dùng nhập query Ví dụ: "Tìm phim về siêu trộm giống Thief hoặc Inception"
🟢 Tạo embedding từ query Dùng nomic-embed-text embed query ➜ ra vector
🟢 Tìm phim trong Mongo Vector search trên Mongo Atlas Vector Search ➜ Lấy top phim liên quan
🔵 Gộp phim thành context Nối thông tin phim thành đoạn text dài ➜ làm context cho prompt
🔥 Gửi vào Llama3.2 Gọi Llama3.2 với prompt: System: Bạn là bot phim. Dữ liệu: [phim]. User: [query]
✅ Trả lời cho user Bot trả lời gọn gàng dựa trên phim đã tìm và knowledge của model

🏗 3. Code tổng quát bro cần
✅ Embedding Search ➜ Mongo vector search

✅ Chat Completion ➜ llama3.2 gọi qua Spring AI (không cần HTTP tay đâu)

🎯 4. Gợi ý cách viết Controller
java
Copy
Edit
@PostMapping("/chat")
public ResponseEntity<String> chat(@RequestBody Map<String, String> request) {
String userQuery = request.get("message");

    // 1. Tìm phim liên quan (vector search)
    List<MovieResponseDTO> relatedMovies = movieSearchService.searchMovies(userQuery);

    // 2. Gộp context từ phim
    StringBuilder contextBuilder = new StringBuilder();
    relatedMovies.forEach(movie -> contextBuilder.append("- ")
        .append(movie.getTitle())
        .append(": ")
        .append(movie.getDescription())
        .append("\\n"));

    String context = contextBuilder.toString();

    // 3. Tạo prompt cho Llama3
    String prompt = """
        Bạn là một trợ lý phim chuyên nghiệp. Dựa vào những phim dưới đây, hãy trả lời câu hỏi người dùng:
        Phim dữ liệu:
        %s
        Câu hỏi người dùng: %s
        """.formatted(context, userQuery);

    // 4. Gọi Llama3 qua Spring AI ChatClient
    String response = chatClient.call(prompt);

    return ResponseEntity.ok(response);

}
🟣 5. Điều kiện bro đã sẵn sàng
✅ Movies bro đã vector hoá (embedding trường trong Mongo)

✅ vectorStore.similaritySearch() hoạt động

✅ ChatClient Spring AI cấu hình gọi Llama3 (Ollama backend)

6. CHi tiết về các cấu trúc đã dùng trong .yml
   ai:
   ollama:
   base-url: http://localhost:11434
   chat:
   options:
   model: llama3.2
   temperature: 0.7
   enabled: true
   embedding:
   model: nomic-embed-text
   chat:
   client:
   enabled: true
   vectorstore:
   mongodb:
   initialize-schema: true
   collection-name: movie_vectors
   index-name: movie_vector_index
   path-name: embedding
   metadata-fields-to-filter: title,genre
   database: MovieInsideForum
