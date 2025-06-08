package com.mif.movieInsideForum.Collection;

import com.mif.movieInsideForum.Module.Movie.Movie;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Document(collection = "actors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Actor {
    @Id
    private ObjectId id;  // Use String instead of ObjectId
    private String name;
    private Date dateOfBirth;
    private String bio;
    private List<Award> awards;
    @DBRef(lazy = true)
    @Builder.Default
    private List<Movie> filmography = new ArrayList<>();
    private String profilePictureUrl;
    @CreatedDate
    private Date createdAt;
    @LastModifiedDate
    private Date updatedAt;
    private Double scoreRank;
    private Double previousScoreRank;
    @Builder.Default
    private List<String> relatedImages = new ArrayList<>();

    // deleted
    private boolean deleted;
    private Integer favoriteCount = 0; // Thêm trường đếm số yêu thích



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Actor actor = (Actor) o;
        return Objects.equals(id, actor.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}

