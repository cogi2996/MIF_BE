package com.mif.movieInsideForum.Module.MovieRating.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.bson.types.ObjectId;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieRatingEventDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId movieId;
    private String newSentiment;
    private String oldSentiment;
    private boolean isDeleted;
} 