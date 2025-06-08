package com.mif.movieInsideForum.Module.ActivityAnalytics.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityMessage {
    private String userId;
    private String groupId; // Optional
    private ActivityMessageTypes activityType;
    private LocalDateTime timestamp;
    private Map<String, Object> metadata;
    
    // Additional fields based on activity type
    private String postId; // For post-related activities
    private String postOwnerId; // For post-related activities
    private String commentId; // For comment-related activities
    private String commentOwnerId; // For comment-related activities
    private String eventId; // For event-related activities
    private String chatId; // For chat-related activities
    private String badgeId; // For badge-related activities
} 