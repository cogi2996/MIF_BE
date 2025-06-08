package com.mif.movieInsideForum.ModelMapperUtil;

import com.mif.movieInsideForum.Collection.Actor;
import com.mif.movieInsideForum.Collection.Director;
import com.mif.movieInsideForum.Collection.Field.Ratings;
import com.mif.movieInsideForum.Module.Movie.Movie;
import com.mif.movieInsideForum.Collection.MovieCategory;
import com.mif.movieInsideForum.Module.Movie.dto.MovieResponseDTO;
import com.mif.movieInsideForum.Module.Movie.dto.MovieRequestDTO;
import com.mif.movieInsideForum.Module.Movie.MovieCategoryRepository;
import com.mif.movieInsideForum.Module.MovieRating.service.MovieRatingsService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MovieConverter {
    private final ModelMapper mapper;
    private final MovieCategoryRepository movieCategoryRepository;
    private final MovieRatingsService movieRatingsService;

    public MovieResponseDTO convertToDTO(Movie movie) {
        MovieResponseDTO movieResponseDTO = mapper.map(movie, MovieResponseDTO.class);
        Ratings ratings = movieRatingsService.getAverageRating(movie.getId());
        movieResponseDTO.setRatings(ratings);
        return movieResponseDTO;
    }

    public MovieResponseDTO convertToDTO(Movie movie, Director director) {
        MovieResponseDTO movieResponseDTO = mapper.map(movie, MovieResponseDTO.class);
        Ratings ratings = movieRatingsService.getAverageRating(movie.getId());
        movieResponseDTO.setRatings(ratings);
        return movieResponseDTO;
    }

    public Movie convertToEntity(MovieRequestDTO movieRequestDTO) {
        Converter<List<ObjectId>, List<Actor>> castConverter = context -> {
            List<ObjectId> castIds = context.getSource();
            if (castIds == null) {
                return List.of(); // Return an empty list if the source is null
            }
            return castIds.stream().map(id -> Actor.builder().id(id).build()).toList();
        };

        Converter<List<ObjectId>, List<Director>> directorConverter = context -> {
            List<ObjectId> directorIds = context.getSource();
            if (directorIds == null) {
                return List.of(); // Return an empty list if the source is null
            }
            return directorIds.stream().map(id -> Director.builder().id(id).build()).toList();
        };

        Converter<List<ObjectId>, List<MovieCategory>> genreConverter = context -> {
            List<ObjectId> genreIds = context.getSource();
            if (genreIds == null) {
                return List.of(); // Return an empty list if the source is null
            }
            return genreIds.stream().map(id -> movieCategoryRepository.findById(id).orElse(null)).toList();
        };

        if (this.mapper.getTypeMap(MovieRequestDTO.class, Movie.class) == null) {
            this.mapper.typeMap(MovieRequestDTO.class, Movie.class).addMappings(mapper -> {
                mapper.skip(Movie::setId);
                mapper.using(castConverter).map(MovieRequestDTO::getCastIds, Movie::setCast);
                mapper.using(directorConverter).map(MovieRequestDTO::getDirectorId, Movie::setDirector);
                mapper.using(genreConverter).map(MovieRequestDTO::getGenreIds, Movie::setGenre);
            });
        }

        return mapper.map(movieRequestDTO, Movie.class);
    }
}