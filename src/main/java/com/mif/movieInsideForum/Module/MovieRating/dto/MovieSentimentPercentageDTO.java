package com.mif.movieInsideForum.Module.MovieRating.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieSentimentPercentageDTO {
    private ObjectId _id;
    private Double positivePercentage;
    private Double negativePercentage;
} 