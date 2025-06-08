package com.mif.movieInsideForum.Module.MovieRating.event;

import com.mif.movieInsideForum.Module.MovieRating.service.MovieSentimentStatsService;
import com.mif.movieInsideForum.Module.MovieRating.dto.MovieRatingEventDTO;
import com.mif.movieInsideForum.Queue.MovieRatingQueueDefine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MovieRatingEventListener {
    private final MovieSentimentStatsService movieSentimentStatsService;

    @RabbitListener(queues = MovieRatingQueueDefine.MOVIE_RATING_QUEUE)
    public void handleRatingEvent(MovieRatingEventDTO event) {
        try {
            log.info("Received rating event for movie {}: {}", event.getMovieId(), event.getNewSentiment());
            movieSentimentStatsService.updateSentimentStats(event);
            log.info("Successfully updated sentiment stats for movie {}", event.getMovieId());
        } catch (Exception e) {
            log.error("Error processing rating event for movie {}: {}", event.getMovieId(), e.getMessage(), e);
            // You might want to implement retry logic or dead letter queue here
        }
    }
} 