package com.mif.movieInsideForum.Collection;

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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "favorite_actors")
public class FavoriteActor {
    @Id
    private ObjectId id;
    private ObjectId userId;
    private ObjectId actorId;
    @CreatedDate
    private Date createdAt;
    @LastModifiedDate
    private Date updatedAt;
}