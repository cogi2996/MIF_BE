package com.mif.movieInsideForum.Module.MovieRating.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieSentimentExtremeDTO {
    private List<MovieSentimentPercentageDTO> mostPositive;
    private List<MovieSentimentPercentageDTO> mostNegative;
} 