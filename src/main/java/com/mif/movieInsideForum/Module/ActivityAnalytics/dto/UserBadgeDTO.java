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
public class UserBadgeDTO {
    private BadgeLevel level;
    private Integer totalScore;
} 