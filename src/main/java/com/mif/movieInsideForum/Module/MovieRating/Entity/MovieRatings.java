package com.mif.movieInsideForum.Module.MovieRating.Entity;

import com.mif.movieInsideForum.Collection.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "movie_ratings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieRatings {
    @Id
    private ObjectId id;
    @Builder.Default
    private Double ratingValue = 0.0;
    private ObjectId movieId;
    @DBRef(lazy = true)
    private User user;
    private String comment;
    
    // Thêm các trường sentiment
    private String sentiment; // POSITIVE, NEGATIVE, NEUTRAL, MIXED
    private Double positiveScore;
    private Double negativeScore;
    private Double neutralScore;
    private Double mixedScore;
    
    @LastModifiedDate
    private Date updatedAt;
    @CreatedDate
    private Date createdAt;
}