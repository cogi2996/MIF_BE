package com.mif.movieInsideForum.Module.MovieRating.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieSentimentStatsDTO {
    private Long positiveCount;
    private Long negativeCount;
    private Long neutralCount;
    private Long mixedCount;
    private Long totalCount;
} 