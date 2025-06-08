package com.mif.movieInsideForum.Module.Post.entity;

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
@Document(collection = "reference_movies")
public class ReferenceMovie {
    @Id
    private ObjectId id;
    
    private ObjectId movieId;
    private String movieName;
    private ObjectId groupId;
    private int referenceCount;
    
    @Builder.Default
    private java.util.Date createdAt = new java.util.Date();
    
    @Builder.Default
    private java.util.Date updatedAt = new java.util.Date();
} 