package com.mif.movieInsideForum.Module.Movie;

import com.mif.movieInsideForum.DTO.ActorResponseDTO;
import com.mif.movieInsideForum.Module.Movie.dto.MovieRequestDTO;
import com.mif.movieInsideForum.Module.Movie.dto.MovieResponseDTO;
import com.mif.movieInsideForum.Module.Movie.dto.MovieUpdateDTO;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MovieService {
    MovieResponseDTO createMovie(MovieRequestDTO movieRequest);

    MovieResponseDTO getMovieById(ObjectId id);

    Slice<MovieResponseDTO> getAllMovies(Pageable pageable);

    boolean updateMovie(ObjectId id, MovieUpdateDTO movieUpdateDTO);

    void deleteMovie(ObjectId id);

    Slice<MovieResponseDTO> getLatestMovie(Pageable pageable);

    List<MovieResponseDTO> get4RandomMovies();

    Slice<MovieResponseDTO> findMoviesByTitle(String title, Pageable pageable);

//    MovieResponseDTO updateMovieDetails(ObjectId id, MovieResponseDTO movieResponseDTO);

    List<ActorResponseDTO> addCast(ObjectId id, List<ObjectId> actorIds);

    Boolean removeCast(ObjectId id, List<ObjectId> actorIds);

    List<ActorResponseDTO> getMovieCast(ObjectId movieId);

    Slice<MovieResponseDTO> findMoviesByCategory(ObjectId categoryId, Pageable pageable);

    Page<MovieResponseDTO> getAllMoviesAsPage(Pageable pageable);

    Page<MovieResponseDTO> findMoviesByCategoryAsPage(ObjectId categoryId, Pageable pageable);

    List<String> getMovieImages(ObjectId id);

    void updateMovieImages(ObjectId id, List<String> newImages);

}