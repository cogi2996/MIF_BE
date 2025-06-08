package com.mif.movieInsideForum.Collection.Field;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum GroupType {
    SMALL(500),
    MEDIUM(1000),
    LARGE(1500);

    @Getter
    private final Integer value;
}