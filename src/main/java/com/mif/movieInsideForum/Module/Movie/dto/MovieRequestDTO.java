package com.mif.movieInsideForum.Module.Movie.dto;

import com.mif.movieInsideForum.Collection.Award;
import com.mif.movieInsideForum.Collection.Field.MovieType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class MovieRequestDTO {
    private String title;
    private String description;
    private Date releaseDate;
    private List<ObjectId> genreIds;
    private List<ObjectId> directorId;
    private List<ObjectId> castIds;
    private String posterUrl;
    private String trailerUrl;
    private Integer duration;
    private String country;
    private Double budget;
    private List<Award> awards;
    private Integer totalEpisodes = null; // Only for TV Series
    private MovieType movieType = MovieType.SINGLE;

}
