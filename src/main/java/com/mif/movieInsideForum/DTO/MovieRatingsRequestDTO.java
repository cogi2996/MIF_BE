package com.mif.movieInsideForum.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
@AllArgsConstructor
@Builder
public class MovieRatingsRequestDTO {
    private Double ratingValue = 0.0;
    private ObjectId movieId;
    private String comment;  // New field for rating content
}