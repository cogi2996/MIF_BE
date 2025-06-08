package com.mif.movieInsideForum.Collection.Field;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum VoteType {
    UPVOTE(1),
    DOWNVOTE(-1);
    @Getter
    private final int value;
}