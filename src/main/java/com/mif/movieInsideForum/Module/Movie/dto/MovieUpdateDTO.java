package com.mif.movieInsideForum.Module.Movie.dto;

import com.mif.movieInsideForum.Collection.Award;
import lombok.Data;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;
@Data

public class MovieUpdateDTO {
    private String title;
    private String description;
    private Date releaseDate;
    private List<ObjectId> genreIds;
    private String posterUrl;
    private String trailerUrl;
    private Integer duration;
    private String country;
    private Double budget;
    private List<Award> awards;
}
