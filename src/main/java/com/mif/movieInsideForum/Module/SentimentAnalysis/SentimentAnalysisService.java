package com.mif.movieInsideForum.Module.SentimentAnalysis;

import com.mif.movieInsideForum.Module.MovieRating.Entity.MovieRatings;
import software.amazon.awssdk.services.comprehend.ComprehendClient;
import software.amazon.awssdk.services.comprehend.model.DetectSentimentRequest;
import software.amazon.awssdk.services.comprehend.model.DetectSentimentResponse;
import software.amazon.awssdk.services.comprehend.model.SentimentType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SentimentAnalysisService {
    private final ComprehendClient comprehendClient;

    public void analyzeSentiment(MovieRatings movieRatings) {
        try {
            DetectSentimentRequest request = DetectSentimentRequest.builder()
                    .text(movieRatings.getComment())
                    .languageCode("en")
                    .build();

            DetectSentimentResponse result = comprehendClient.detectSentiment(request);

            // Set sentiment trực tiếp vào MovieRatings
            movieRatings.setSentiment(result.sentiment().toString());
            movieRatings.setPositiveScore(result.sentimentScore().positive().doubleValue());
            movieRatings.setNegativeScore(result.sentimentScore().negative().doubleValue());
            movieRatings.setNeutralScore(result.sentimentScore().neutral().doubleValue());
            movieRatings.setMixedScore(result.sentimentScore().mixed().doubleValue());
        } catch (Exception e) {
            // Fallback to neutral sentiment
            movieRatings.setSentiment(SentimentType.NEUTRAL.toString());
            movieRatings.setPositiveScore(0.0);
            movieRatings.setNegativeScore(0.0);
            movieRatings.setNeutralScore(1.0);
            movieRatings.setMixedScore(0.0);
        }
    }
}