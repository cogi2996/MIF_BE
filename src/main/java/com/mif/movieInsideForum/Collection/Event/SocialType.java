package com.mif.movieInsideForum.Collection.Event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum SocialType {
    OTHER("other_link"),
    MIF_LIVE("mif_live");
    @Getter
    private final String name;
}
