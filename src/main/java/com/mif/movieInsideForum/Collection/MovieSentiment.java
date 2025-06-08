package com.mif.movieInsideForum.Collection;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "movie_sentiments")
public class MovieSentiment {
    @Id
    private String id;
    private String movieId;
    private String userId;
    private String commentId;
    private String sentiment; // POSITIVE, NEGATIVE, NEUTRAL, MIXED
    private Double positiveScore;
    private Double negativeScore;
    private Double neutralScore;
    private Double mixedScore;
    private String originalText;
}