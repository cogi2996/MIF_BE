package com.mif.movieInsideForum.Collection.Notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NotificationType {
    UP_VOTE("upvote"), // Cần postId
    DOWN_VOTE("down_vote"), // Cần postId
    REMOVE_VOTE("remove_vote"), // Cần postId
    COMMENT("comment"), // Cần postId
    JOIN_REQUEST("join_request"), // Cần groupId
    ACCEPT_REQUEST("accept_request"), // Cần groupId
    EVENT("event"), // Cần eventId
    BADGE_EARNED("badge_earned"), // Cần badgeLevel
    POST_BLOCKED("post_blocked"), // Cần postId và groupId
    REPORT_REJECTED("report_rejected"); // Thông báo khi báo cáo bị từ chối
    private final String value; // Giá trị của loại thông báo

}