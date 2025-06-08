package com.mif.movieInsideForum.Module.Movie.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mif.movieInsideForum.Collection.Award;
import com.mif.movieInsideForum.Collection.Field.MovieType;
import com.mif.movieInsideForum.Collection.Field.Ratings;
import com.mif.movieInsideForum.Collection.MovieCategory;
import com.mif.movieInsideForum.DTO.DirectorResponseDTO;
import com.mif.movieInsideForum.DTO.ActorResponseDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class MovieResponseDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String title;
    private String description;
    private Date releaseDate;
    private List<MovieCategory> genre;
    private List<ActorResponseDTO> cast;
    private List<DirectorResponseDTO> director = new ArrayList<>();
    private Ratings ratings;
    private String posterUrl;
    private String trailerUrl;
    private Integer duration;
    private String country;
    private MovieType movieType = MovieType.SINGLE;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer totalEpisodes = null; // Only for TV Series
    private Double budget;
    private List<Award> awards;

}
