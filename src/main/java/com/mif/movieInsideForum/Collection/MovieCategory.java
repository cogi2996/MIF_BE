package com.mif.movieInsideForum.Collection;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "movie_categories")
@Builder
public class MovieCategory {
    @JsonSerialize(using = ToStringSerializer.class)
    @Id
    private ObjectId id;
    private String categoryName; // e.g., "Movies", "Actors", "Awards", etc.
    private String description; // Optional description of the category
    @CreatedDate
    private Date createdAt;
    @LastModifiedDate
    private Date updatedAt;
}
