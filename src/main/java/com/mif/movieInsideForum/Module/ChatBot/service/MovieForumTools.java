package com.mif.movieInsideForum.Module.ChatBot.service;

import org.springframework.stereotype.Service;

import com.mif.movieInsideForum.Collection.Event.Event;
import com.mif.movieInsideForum.Module.ChatBot.OpenAIService;
import com.mif.movieInsideForum.Module.ChatBot.repository.ChatHistoryRepository;
import com.mif.movieInsideForum.Module.Event.EventService;
import com.mif.movieInsideForum.Module.Post.DTO.GroupPostResponseDTO;
import com.mif.movieInsideForum.Module.Post.service.GroupPostService;
import com.mif.movieInsideForum.Security.AuthenticationFacade;

import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import org.bson.types.ObjectId;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.security.core.Authentication;


@Service
@RequiredArgsConstructor
public class MovieForumTools {
    private final GroupPostService groupPostService;
    private final EventService eventService;
    private final OpenAIService openAIService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private final AuthenticationFacade authenticationFacade;
    private final ChatHistoryRepository chatHistoryRepository;
    

    // get userId from authentication
    private String getUserId() {
        Authentication authentication = authenticationFacade.getAuthentication();
        return authentication.getName();
    }

    @Tool(name = "get_top_upvoted_posts", description = "Lấy top 5 bài viết được upvote nhiều nhất")
    public String getTopUpvotedPosts() {
        List<GroupPostResponseDTO> topPosts = groupPostService.getTop5MostUpvotedPosts();
        String response = formatPostsToMarkdown(topPosts);
        return response;
    }

    @Tool(name = "get_trending_posts", description = "Lấy top 5 bài viết trending trong tuần")
    public Map<String, Object> getTrendingPosts() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date startDate = calendar.getTime();
        
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        Date endDate = calendar.getTime();
        
        List<GroupPostResponseDTO> trendingPosts = groupPostService.getTrendingPostsInWeek(startDate, endDate);
        String response = formatTrendingPostsToMarkdown(trendingPosts);
        
        
        
        Map<String, Object> result = new HashMap<>();
        result.put("response", response);
        result.put("posts", trendingPosts);
        return result;
    }

    @Tool(name = "get_upcoming_events", description = "Lấy danh sách các sự kiện mà người dùng đã tham gia sắp đến")
    public String getUpcomingEvents() {
        String userId = getUserId();
        ObjectId userIdObj = new ObjectId(userId);
        List<Event> events = eventService.getUpcomingSubscribedEvents(userIdObj);
        String response = formatEventsToMarkdown(events);
        
            
        
        return response;
    }

    @Tool(name = "search_movie", description = "Tìm kiếm phim theo từ khóa")
    public Map<String, Object> searchMovie(
        @ToolParam(description = "Lấy chính xác câu hỏi của người dùng đang tìm kiếm") String keyword
    ) {
        return openAIService.searchMoviesWithOpenAI(keyword, getUserId());
    }

    @Tool(name = "clear_history", description = "Xóa lịch sử chat")
    public String clearHistory() {
        String userId = getUserId();
        chatHistoryRepository.deleteByUserId(userId);
        return "Đã xóa lịch sử chat của bạn";
    }

    // Các phương thức helper
    private String formatPostsToMarkdown(List<GroupPostResponseDTO> posts) {
        if (posts.isEmpty()) {
            return "Hiện tại chưa có bài viết nào được upvote cả 🥲";
        }
    
        StringBuilder sb = new StringBuilder();
        sb.append("Dưới đây là top 5 bài viết được upvote nhiều nhất nè 👇<br><br>");
        
        for (GroupPostResponseDTO post : posts) {
            String postTitleWithLink = String.format("[%s](/groups/%s/posts/%s)",
                truncateString(post.getTitle(), 30),
                post.getGroupId(),
                post.getId()
            );
            sb.append("• ")
              .append(postTitleWithLink)
              .append("<br>👤 ")
              .append(post.getOwner().getDisplayName())
              .append(" - ( ")
              .append(post.getVoteNumber())
              .append(" upvotes )")
              .append("<br><br>");
        }
        
        return sb.toString().trim();
    }

    private String formatTrendingPostsToMarkdown(List<GroupPostResponseDTO> posts) {
        if (posts.isEmpty()) {
            return "Hiện tại chưa có bài viết nào trending trong tuần này 🥲";
        }
    
        StringBuilder sb = new StringBuilder();
        sb.append("Dưới đây là top 5 bài viết trending trong tuần này nè 👇<br><br>");
        
        for (GroupPostResponseDTO post : posts) {
            String postTitleWithLink = String.format("[%s](/groups/%s/posts/%s)",
                truncateString(post.getTitle(), 30),
                post.getGroupId(),
                post.getId()
            );
            sb.append("• ")
              .append(postTitleWithLink)
              .append("<br>👤 ")
              .append(post.getOwner().getDisplayName())
              .append("<br>📊 ")
              .append(post.getVoteNumber())
              .append(" upvotes")
              .append("<br><br>");
        }
        
        return sb.toString().trim();
    }

    private String formatEventsToMarkdown(List<Event> events) {
        if (events.isEmpty()) {
            return "Hiện tại bạn chưa đăng ký tham gia sự kiện nào sắp tới cả 🥲. Hãy khám phá các nhóm để tham gia nhé!";
        }
    
        StringBuilder sb = new StringBuilder();
        sb.append("Dưới đây là các sự kiện mà bạn đã đăng ký và sắp diễn ra nè 👇<br><br>");
        for (Event event : events) {
            String eventNameWithLink = String.format("[%s](/groups/%s)",
                truncateString(event.getEventName(), 25),
                event.getGroupId()
            );
            sb.append("• ")
              .append(eventNameWithLink)
              .append("<br>🗓 ")
              .append(dateFormat.format(event.getStartDate()))
              .append("<br><br>");
        }
        sb.append("Tui cũng có gửi mail cho bạn đó! 🕒");
        return sb.toString().trim();
    }

    private String truncateString(String str, int maxLength) {
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }
}
