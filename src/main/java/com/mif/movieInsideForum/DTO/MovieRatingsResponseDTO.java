package com.mif.movieInsideForum.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.bson.types.ObjectId;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieRatingsResponseDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private Double ratingValue = 0.0;
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId movieId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserDTO user;
    private String comment;
    private Date updatedAt;
    private Date createdAt;
    
    // Sentiment information
    private String sentiment;
    private Double positiveScore;
    private Double negativeScore;
    private Double neutralScore;
    private Double mixedScore;
}
