package com.mif.movieInsideForum.Module.SentimentAnalysis;

import com.mif.movieInsideForum.Module.MovieRating.Entity.MovieRatings;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sentiment")
@RequiredArgsConstructor
public class SentimentAnalysisController {

    private final SentimentAnalysisService sentimentAnalysisService;

    @PostMapping("/analyze")
    public ResponseEntity<MovieRatings> analyzeSentiment(@RequestBody MovieRatings movieRatings) {
        sentimentAnalysisService.analyzeSentiment(movieRatings);
        return ResponseEntity.ok(movieRatings);
    }
}