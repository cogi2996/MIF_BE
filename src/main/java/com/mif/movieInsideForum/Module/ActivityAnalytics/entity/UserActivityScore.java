package com.mif.movieInsideForum.Module.ActivityAnalytics.entity;

import com.mif.movieInsideForum.Module.ActivityAnalytics.enums.BadgeLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_activity_scores")
public class UserActivityScore {
    @Id
    private String id;
    private String userId;
    private String groupId;
    
    @Builder.Default
    private Integer totalScore = 0;
    
    @Builder.Default
    private Integer groupJoinScore = 0;
    
    @Builder.Default
    private Integer postScore = 0;
    
    @Builder.Default
    private Integer commentScore = 0;
    
    @Builder.Default
    private Integer likeScore = 0;
    
    @Builder.Default
    private Integer receivedLikeScore = 0;
    
    @Builder.Default
    private Integer receivedCommentScore = 0;
    
    @Builder.Default
    private Integer eventScore = 0;
    
    private BadgeLevel badgeLevel;
    
    @Builder.Default
    private LocalDateTime lastUpdated = LocalDateTime.now();
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
} 