package com.mif.movieInsideForum.Module.MovieRating.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SentimentStatsDTO {
    private Long totalComments;
    private Long positiveCount;
    private Long negativeCount;
    private Long neutralCount;
} 