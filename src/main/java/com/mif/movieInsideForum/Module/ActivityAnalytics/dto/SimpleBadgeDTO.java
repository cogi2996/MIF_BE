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
public class SimpleBadgeDTO {
    private BadgeLevel level;
    // private String name;
} 