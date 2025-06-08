package com.mif.movieInsideForum.Module.Movie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieSentimentStatsDTO {
    private int totalComments;
    private double positivePercentage;
    private double negativePercentage;
    private double neutralPercentage;
    private String mostPositiveMovie;
    private double mostPositivePercentage;
    private String mostNegativeMovie;
    private double mostNegativePercentage;
    private String lastUpdated;
} 