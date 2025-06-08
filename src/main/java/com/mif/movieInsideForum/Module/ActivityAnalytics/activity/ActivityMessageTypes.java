package com.mif.movieInsideForum.Module.ActivityAnalytics.activity;

public enum ActivityMessageTypes {
    // Group related activities
    GROUP_JOINED("group.joined"),
    
    // Post related activities (only in groups)
    GROUP_POST_CREATED("group.post.created"),
    GROUP_POST_LIKED("group.post.liked"),
    
    // Comment related activities (only in group posts)
    GROUP_COMMENT_CREATED("group.comment.created"),
    GROUP_COMMENT_LIKED("group.comment.liked"),
    
    // Event related activities (only in groups)
    GROUP_EVENT_JOINED("group.event.joined"),

    // Badge related activities
    BADGE_EARNED("badge.earned");

    private final String routingKey;

    ActivityMessageTypes(String routingKey) {
        this.routingKey = routingKey;
    }

    public String getRoutingKey() {
        return routingKey;
    }
} 