package com.mif.movieInsideForum.Module.MovieRating.service;

import com.mif.movieInsideForum.Module.MovieRating.Entity.MovieSentimentStats;
import com.mif.movieInsideForum.Module.MovieRating.repository.MovieSentimentStatsRepository;
import com.mif.movieInsideForum.Module.MovieRating.dto.MovieRatingEventDTO;
import com.mif.movieInsideForum.Module.MovieRating.dto.MovieSentimentStatsDTO;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MovieSentimentStatsServiceImpl implements MovieSentimentStatsService {
    private final MovieSentimentStatsRepository movieSentimentStatsRepository;

    @Override
    @Transactional
    public void updateSentimentStats(MovieRatingEventDTO event) {
        MovieSentimentStats stats = movieSentimentStatsRepository.findByMovieId(event.getMovieId())
                .orElse(MovieSentimentStats.builder()
                        .movieId(event.getMovieId())
                        .positiveCount(0L)
                        .negativeCount(0L)
                        .neutralCount(0L)
                        .mixedCount(0L)
                        .totalCount(0L)
                        .build());

        if (event.isDeleted()) {
            // Decrease count for the old sentiment
            decreaseSentimentCount(stats, event.getOldSentiment());
            stats.setTotalCount(stats.getTotalCount() - 1);
        } else {
            // If this is an update (old sentiment exists), decrease the old sentiment count
            if (event.getOldSentiment() != null) {
                decreaseSentimentCount(stats, event.getOldSentiment());
            }
            
            // Increase count for the new sentiment
            if (event.getNewSentiment() != null) {
                increaseSentimentCount(stats, event.getNewSentiment());
            }

            // Total count only increases for new ratings, not for updates
            if (event.getOldSentiment() == null) {
                stats.setTotalCount(stats.getTotalCount() + 1);
            }
        }

        movieSentimentStatsRepository.save(stats);
    }

    private void decreaseSentimentCount(MovieSentimentStats stats, String sentiment) {
        if (sentiment == null) return;
        
        switch (sentiment) {
            case "POSITIVE" -> stats.setPositiveCount(stats.getPositiveCount() - 1);
            case "NEGATIVE" -> stats.setNegativeCount(stats.getNegativeCount() - 1);
            case "NEUTRAL" -> stats.setNeutralCount(stats.getNeutralCount() - 1);
            case "MIXED" -> stats.setMixedCount(stats.getMixedCount() - 1);
        }
    }

    private void increaseSentimentCount(MovieSentimentStats stats, String sentiment) {
        if (sentiment == null) return;
        
        switch (sentiment) {
            case "POSITIVE" -> stats.setPositiveCount(stats.getPositiveCount() + 1);
            case "NEGATIVE" -> stats.setNegativeCount(stats.getNegativeCount() + 1);
            case "NEUTRAL" -> stats.setNeutralCount(stats.getNeutralCount() + 1);
            case "MIXED" -> stats.setMixedCount(stats.getMixedCount() + 1);
        }
    }

    @Override
    public MovieSentimentStatsDTO getMovieSentimentStats(ObjectId movieId) {
        MovieSentimentStats stats = movieSentimentStatsRepository.findByMovieId(movieId)
                .orElse(MovieSentimentStats.builder()
                        .movieId(movieId)
                        .positiveCount(0L)
                        .negativeCount(0L)
                        .neutralCount(0L)
                        .mixedCount(0L)
                        .totalCount(0L)
                        .build());

        return MovieSentimentStatsDTO.builder()
                .positiveCount(stats.getPositiveCount())
                .negativeCount(stats.getNegativeCount())
                .neutralCount(stats.getNeutralCount())
                .mixedCount(stats.getMixedCount())
                .totalCount(stats.getTotalCount())
                .build();
    }
} 