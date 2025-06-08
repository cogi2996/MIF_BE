package com.mif.movieInsideForum.Module.Movie;

import com.mif.movieInsideForum.Module.Movie.dto.MovieResponseDTO;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Map;

public interface SavedMovieService {
    void saveMovie(ObjectId userId, ObjectId movieId);
    void unsaveMovie(ObjectId userId, ObjectId movieId);
    boolean isMovieSaved(ObjectId userId, ObjectId movieId);
    Slice<MovieResponseDTO> getSavedMovies(ObjectId userId, Pageable pageable);
    Map<String, Boolean> batchCheckSavedMoviesStatus(ObjectId userId, List<ObjectId> movieIds);
}