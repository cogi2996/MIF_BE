Quá đỉnh luôn bro, bạn đang xài **Spring AI** thì triển khai **MCP (Modular Command Pattern)** sẽ cực kỳ mượt, vì Spring AI hỗ trợ function calling, schema-based response, và bạn dễ dàng plug các handler vào để xử lý theo `intent`.

---

## 🔥 Hướng tiếp cận để làm MCP trong Spring AI

### ✅ 1. Mục tiêu:

- Người dùng nhập: `"Tìm phim có rồng"`
- Chat model trả về:

```json
{
  "intent": "search_movie",
  "keyword": "rồng"
}
```

- Spring AI sẽ route sang `SearchMovieHandler` để xử lý.

---

## ⚙️ Các bước triển khai:

---

### 🧩 Bước 1: Định nghĩa Schema cho từng Intent

```java
public record SearchMovieFunction(
    String intent,
    String keyword
) {}
```

> Sau đó dùng `OpenAiChatOptions.builder().schema(...)` để ép model trả về dạng JSON.

---

### 🧠 Bước 2: Tạo interface cho handler (MCP-style)

```java
public interface CommandHandler {
    String getIntent();
    String handle(Map<String, Object> params);
}
```

---

### 🧱 Bước 3: Viết các handler riêng

```java
@Component
public class SearchMovieHandler implements CommandHandler {

    @Override
    public String getIntent() {
        return "search_movie";
    }

    @Override
    public String handle(Map<String, Object> params) {
        String keyword = (String) params.get("keyword");
        return movieSearchService.searchMovies(keyword).toString();
    }
}
```

```java
@Component
public class ClearHistoryHandler implements CommandHandler {
    @Override
    public String getIntent() {
        return "clear_history";
    }

    @Override
    public String handle(Map<String, Object> params) {
        return chatHistoryRepository.clearHistory((String) params.get("userId"));
    }
}
```

---

### 🔄 Bước 4: Đăng ký và route đến handler phù hợp

```java
@Service
public class DispatcherService {

    private final Map<String, CommandHandler> handlers;

    public DispatcherService(List<CommandHandler> handlerList) {
        this.handlers = handlerList.stream()
            .collect(Collectors.toMap(CommandHandler::getIntent, h -> h));
    }

    public String dispatch(Map<String, Object> resultMap) {
        String intent = (String) resultMap.get("intent");
        CommandHandler handler = handlers.get(intent);
        if (handler != null) {
            return handler.handle(resultMap);
        }
        return "Không hiểu ý bạn rồi bro 😓";
    }
}
```

---

### 📦 Bước 5: Gọi OpenAI và parse intent

```java
public Map<String, Object> analyzeIntent(String userInput) {
    Prompt prompt = new Prompt(List.of(
        new SystemMessage("Trích xuất intent từ câu hỏi và trả về dạng JSON"),
        new UserMessage(userInput)
    ), ChatOptions.builder()
        .withTemperature(0.2f)
        .build()
    );

    ChatResponse response = chatModel.call(prompt);
    return parseJson(response.getResult().getOutput().getContent());
}
```

---

### 🎯 Bước 6: Tích hợp vào controller

```java
@PostMapping("/chat")
public ResponseEntity<String> chat(@RequestBody String userInput) {
    Map<String, Object> intentData = openAIService.analyzeIntent(userInput);
    String reply = dispatcherService.dispatch(intentData);
    return ResponseEntity.ok(reply);
}
```

---

## 🎁 Kết quả:

- Mỗi intent có handler riêng.
- Mở rộng cực dễ: chỉ cần thêm handler mới.
- Chatbot phản hồi theo ngữ cảnh và chức năng đúng.

# Hướng dẫn render response từ Chatbot MCP cho Next.js (Frontend Dev)

## Example response mẫu cho từng intent

### 1. search_movie

```json
{
    "status": "success",
    "message": "Xử lý yêu cầu thành công",
    "data": {
        "movies": [
            {
                "id": "683f3856a2e9bf406bb67001",
                "title": "Fight Club",
                "description": "Một nhân viên văn phòng mất ngủ kết hợp với một thợ làm xà phòng lập câu lạc bộ đánh nhau ngầm.!",
                "releaseDate": "2020-12-18T00:00:00.000+00:00",
                "genre": [
                    { "id": "...", "categoryName": "Hài kịch", ... },
                    { "id": "...", "categoryName": "Mạo hiểm", ... }
                ],
                "director": [
                    { "id": "...", "name": "Christopher Nolan", ... }
                ],
                "posterUrl": "...",
                "duration": 155,
                "country": "algeria",
                "movieType": "SERIES",
                "totalEpisodes": 3,
                ...
            }
        ],
        "semanticQuery": "phim về ông bị bệnh mất ngủ hợp tác làm xà phòng lập clb đánh nhau",
        "response": "Phim bạn đang nói đến là \"Fight Club\". Nội dung phim xoay quanh một nhân viên văn phòng bị mất ngủ, người đã kết hợp với một thợ làm xà phòng để thành lập một câu lạc bộ đánh nhau ngầm. ..."
    },
    "type": "search_movie"
}
```

### 2. get_upcoming_events

```json
{
  "status": "success",
  "message": "Xử lý yêu cầu thành công",
  "data": "Dưới đây là các sự kiện mà bạn đã đăng ký và sắp diễn ra nè 👇\n\n• [Buổi thảo luận phim Av...](/groups/68103f34af840f0e1786ae63)\n🗓 08/06/2025 21:27\n\n• [Buổi thảo luận phim ch...](/groups/68103f34af840f0e1786ae63)\n🗓 08/06/2025 21:27\n\n• [Buổi thảo luận phim ch...](/groups/68103f34af840f0e1786ae63)\n🗓 08/06/2025 21:27\n\n• [Buổi thảo luận phim si...](/groups/68103f34af840f0e1786ae63)\n🗓 08/06/2025 21:27\n\nTui cũng có gửi mail cho bạn đó! 🕒",
  "type": "get_upcoming_events"
}
```

### 3. clear_history

```json
{
  "status": "success",
  "message": "Xử lý yêu cầu thành công",
  "data": "Đã xóa lịch sử chat của bạn",
  "type": "clear_history"
}
```

---

## 1. SearchMovieHandler

- **Intent:** `search_movie`
- **Trả về:** Danh sách phim (object/list hoặc markdown table/list)
- **Render:**
  - Nếu là list object: render dạng card/list phim.
  - Nếu là markdown table/list: parse và render thành bảng hoặc danh sách.
  - Nếu có link phim: bọc tên phim bằng thẻ `<a>`.

## 2. ClearChatHistoryHandler

- **Intent:** `clear_history`
- **Trả về:** Chuỗi thông báo đơn giản (string)
- **Render:**
  - Hiển thị message dạng alert hoặc text thông thường.

## 3. GetUpcomingEventsHandler

- **Intent:** `get_upcoming_events`
- **Trả về:**
  - **Dạng list markdown:**
    ```
    • [Tên sự kiện](/groups/{groupId})
      🗓 08/06/2025 21:27
    ```
- **Render:**
  - Parse markdown, mỗi sự kiện là một dòng.
  - Tên sự kiện là link, click chuyển đến `/groups/{groupId}`.
  - Ngày bắt đầu hiển thị ngay dưới tên sự kiện, có thể thêm icon lịch.
  - Nếu không có sự kiện, hiển thị message động viên/gợi ý tham gia nhóm.

## 4. Handler khác (tuỳ mở rộng)

- **Intent:** tuỳ handler mới
- **Trả về:** markdown, plain text, json, object...
- **Render:**
  - Đọc intent, xác định handler, parse và render phù hợp.
  - Nếu là markdown: dùng thư viện markdown (`react-markdown`...)
  - Nếu là plain text: hiển thị như message chat.
  - Nếu là object: mapping UI phù hợp (list phim, list sự kiện...)

---

### Lưu ý chung cho Next.js:

- Luôn kiểm tra intent trong response để xác định cách render.
- Nếu response là markdown, dùng thư viện như `react-markdown` để hiển thị.
- Nếu response là plain text, hiển thị như message chat.
- Nếu response là object, cần mapping UI phù hợp.

---

**Tóm lại:**

- Mỗi intent có handler riêng, frontend chỉ cần dựa vào intent để render đúng UI.
- Đặc biệt với intent như `get_upcoming_events`, cần parse markdown list và render link đúng chuẩn Next.js.
