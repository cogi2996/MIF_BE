package com.mif.movieInsideForum.Module.MovieRating.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SentimentCountDTO {
    private String sentiment;
    private Long count;
} 