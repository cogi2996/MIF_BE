package com.mif.movieInsideForum.Module.ActivityAnalytics.enums;

import lombok.Getter;

@Getter
public enum BadgeLevel {
    BRONZE("BRONZE"),
    SILVER("SILVER"),
    GOLD("GOLD"),
    PLATINUM("PLATINUM");

    private final String level;

    BadgeLevel(String level) {
        this.level = level;
    }
} 