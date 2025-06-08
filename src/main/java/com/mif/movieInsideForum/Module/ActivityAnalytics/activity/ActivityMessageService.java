package com.mif.movieInsideForum.Module.ActivityAnalytics.activity;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ActivityMessageService {
    private final RabbitTemplate rabbitTemplate;
    private static final String EXCHANGE_NAME = "activity.exchange";

    public void sendActivityMessage(ActivityMessage message) {
        rabbitTemplate.convertAndSend(
            EXCHANGE_NAME,
            message.getActivityType().getRoutingKey(),
            message
        );
    }

    // Group related activities
    public void sendGroupJoined(String userId, String groupId) {
        ActivityMessage message = ActivityMessage.builder()
            .userId(userId)
            .groupId(groupId)
            .activityType(ActivityMessageTypes.GROUP_JOINED)
            .timestamp(LocalDateTime.now())
            .build();
        sendActivityMessage(message);
    }

    // Post related activities in groups
    public void sendGroupPostCreated(String userId, String postId, String groupId, String postOwnerId) {
        ActivityMessage message = ActivityMessage.builder()
            .userId(userId)
            .postId(postId)
            .groupId(groupId)
            .postOwnerId(postOwnerId)
            .activityType(ActivityMessageTypes.GROUP_POST_CREATED)
            .timestamp(LocalDateTime.now())
            .build();
        sendActivityMessage(message);
    }

    public void sendGroupPostLiked(String userId, String postId, String groupId, String postOwnerId) {
        ActivityMessage message = ActivityMessage.builder()
            .userId(userId)
            .postId(postId)
            .groupId(groupId)
            .postOwnerId(postOwnerId)
            .activityType(ActivityMessageTypes.GROUP_POST_LIKED)
            .timestamp(LocalDateTime.now())
            .build();
        sendActivityMessage(message);
    }

    // Comment related activities in group posts
    public void sendGroupCommentCreated(String userId, String commentId, String postId, String groupId, String postOwnerId, String commentOwnerId) {
        ActivityMessage message = ActivityMessage.builder()
            .userId(userId)
            .commentId(commentId)
            .postId(postId)
            .groupId(groupId)
            .postOwnerId(postOwnerId)
            .commentOwnerId(commentOwnerId)
            .activityType(ActivityMessageTypes.GROUP_COMMENT_CREATED)
            .timestamp(LocalDateTime.now())
            .build();
        sendActivityMessage(message);
    }

    public void sendGroupCommentLiked(String userId, String commentId, String postId, String groupId, String commentOwnerId) {
        ActivityMessage message = ActivityMessage.builder()
            .userId(userId)
            .commentId(commentId)
            .postId(postId)
            .groupId(groupId)
            .commentOwnerId(commentOwnerId)
            .activityType(ActivityMessageTypes.GROUP_COMMENT_LIKED)
            .timestamp(LocalDateTime.now())
            .build();
        sendActivityMessage(message);
    }

    // Event related activities in groups
    public void sendGroupEventJoined(String userId, String eventId, String groupId) {
        ActivityMessage message = ActivityMessage.builder()
            .userId(userId)
            .eventId(eventId)
            .groupId(groupId)
            .activityType(ActivityMessageTypes.GROUP_EVENT_JOINED)
            .timestamp(LocalDateTime.now())
            .build();
        sendActivityMessage(message);
    }

    // Badge related activities
    public void sendBadgeEarned(String userId, String badgeId, String groupId) {
        ActivityMessage message = ActivityMessage.builder()
            .userId(userId)
            .badgeId(badgeId)
            .groupId(groupId)
            .activityType(ActivityMessageTypes.BADGE_EARNED)
            .timestamp(LocalDateTime.now())
            .build();
        sendActivityMessage(message);
    }
} 