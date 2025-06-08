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

@Document(collection = "directors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Director {
    @Id
    private ObjectId id;
    private String name;
    private Date dateOfBirth;
    private String bio;
    @DBRef(lazy = true)
    @Builder.Default
    private List<Movie> filmography = new ArrayList<>();
    private List<Award> awards;
    private String profilePictureUrl;
    @CreatedDate
    private Date createdAt;
    @LastModifiedDate
    private Date updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Director director = (Director) o;
        return Objects.equals(id, director.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}