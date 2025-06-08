package com.mif.movieInsideForum.Module.Movie;

import com.mif.movieInsideForum.Collection.SavedMovie;
import com.mif.movieInsideForum.Collection.User;
import com.mif.movieInsideForum.Module.Movie.dto.MovieResponseDTO;
import com.mif.movieInsideForum.ModelMapperUtil.MovieConverter; // Assuming you have a converter for Movie to MovieResponseDTO
import com.mif.movieInsideForum.Module.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SavedMovieServiceImpl implements SavedMovieService {
    private final SavedMovieRepository savedMovieRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final MovieConverter movieConverter; // Assuming you have a converter

    @Override
    public void saveMovie(ObjectId userId, ObjectId movieId) {
        if (!savedMovieRepository.existsByUserIdAndMovieId(userId, movieId)) {
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new RuntimeException("Movie not found"));
            SavedMovie savedMovie = new SavedMovie();
            savedMovie.setUser(user);
            savedMovie.setMovie(movie);
            savedMovieRepository.save(savedMovie);
        }
    }

    @Override
    public void unsaveMovie(ObjectId userId, ObjectId movieId) {
        savedMovieRepository.deleteByUserIdAndMovieId(userId, movieId);
    }

    @Override
    public boolean isMovieSaved(ObjectId userId, ObjectId movieId) {
        return savedMovieRepository.existsByUserIdAndMovieId(userId, movieId);
    }

    @Override
    public Slice<MovieResponseDTO> getSavedMovies(ObjectId userId, Pageable pageable) {
         Optional<Slice<SavedMovie> > savedMovies = savedMovieRepository.findByUserId(userId, pageable);
        // Return an empty slice if no saved movies are found
        return savedMovies.map(movies -> movies.map(savedMovie -> movieConverter.convertToDTO(savedMovie.getMovie()))).orElseGet(() -> new SliceImpl<>(List.of(), pageable, false));

    }

    @Override
    public Map<String, Boolean> batchCheckSavedMoviesStatus(ObjectId userId, List<ObjectId> movieIds) {
        List<SavedMovie> savedMovies = savedMovieRepository.findByUserIdAndMovieIdIn(userId, movieIds);
        Map<String, Boolean> result = new HashMap<>();
        for (ObjectId movieId : movieIds) {
            result.put(movieId.toHexString(), false);
        }
        for (SavedMovie savedMovie : savedMovies) {
            String movieId = savedMovie.getMovie().getId().toHexString();
            result.put(movieId, true);
        }
        return result;
    }
}