package com.mif.movieInsideForum.Module.Movie;

import com.mif.movieInsideForum.Collection.Actor;
import com.mif.movieInsideForum.Collection.Award;
import com.mif.movieInsideForum.Collection.Director;
import com.mif.movieInsideForum.Collection.Field.MovieType;
import com.mif.movieInsideForum.Collection.MovieCategory;
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

@Document(collection = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {
    @Id
    private ObjectId id;
    private String title;
    private String description;
    private Date releaseDate;
    @DBRef(lazy = true)
    private List<MovieCategory> genre;
    @DBRef(lazy = true)
    @Builder.Default
    private List<Director> director = new ArrayList<>();
    @Builder.Default
    private List<String> relatedImages = new ArrayList<>();
    @DBRef(lazy = true)
    @Builder.Default
    private List<Actor> cast = new ArrayList<>();
    private String posterUrl;
    private String trailerUrl;
    private Integer duration;
    private String country;
    private Double budget;
    private List<Award> awards;
    @CreatedDate
    private Date createdAt;
    @LastModifiedDate
    private Date updatedAt;
    @Builder.Default
    private MovieType movieType = MovieType.SINGLE;
    private Integer totalEpisodes = null; // Only for TV Series
    private List<Double> embed; // Vector embedding for semantic search
    private Double score; // Vector search score

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(id, movie.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}