package com.mif.movieInsideForum.Module.ActivityAnalytics.dto;

import com.mif.movieInsideForum.Module.ActivityAnalytics.enums.BadgeLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeNotificationDTO {
    private String userId;
    private String groupId;
    private BadgeLevel badgeLevel;
    private String content;
    private String type;
} 