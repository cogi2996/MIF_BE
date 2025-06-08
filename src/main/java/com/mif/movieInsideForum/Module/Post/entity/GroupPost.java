package com.mif.movieInsideForum.Module.Post.entity;

import com.mif.movieInsideForum.Collection.Group;
import com.mif.movieInsideForum.Collection.User;
import com.mif.movieInsideForum.Module.Movie.dto.MovieResponseDTO;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.*;

@Document(collection = "group_posts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = {"group", "owner"})
public class GroupPost implements Serializable {
    @Id
    private ObjectId id = new ObjectId();
    private String title;
    @DBRef(lazy = true)
    private Group group;
    // hidden field
    @DBRef(lazy = true)
    private User owner;
    private String content;
    private List<String> mediaUrls;
    private List<MovieResponseDTO> mentionedMovies;
    private Integer ratingCount = 0;
    @Builder.Default
    private Date createdAt = new Date();
    @LastModifiedDate
    private Date updatedAt;
    @Builder.Default
    private Boolean isBlock = false;

    public ObjectId getGroupId() {
        return group.getId();
    }
}
