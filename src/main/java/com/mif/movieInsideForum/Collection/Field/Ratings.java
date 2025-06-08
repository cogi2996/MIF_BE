package com.mif.movieInsideForum.Collection.Field;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Ratings {
    private Double averageRating;
    private Integer numberOfRatings;
}