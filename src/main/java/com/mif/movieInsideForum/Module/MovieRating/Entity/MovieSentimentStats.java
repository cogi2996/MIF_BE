package com.mif.movieInsideForum.Module.MovieRating.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "movie_sentiment_stats")
public class MovieSentimentStats {
    @Id
    private ObjectId id;
    private ObjectId movieId;
    private Long positiveCount;
    private Long negativeCount;
    private Long neutralCount;
    private Long mixedCount;
    private Long totalCount;
} 