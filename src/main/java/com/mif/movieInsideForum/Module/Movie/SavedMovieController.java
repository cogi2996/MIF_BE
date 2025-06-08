package com.mif.movieInsideForum.Module.Movie;

import com.mif.movieInsideForum.DTO.BatchCheckRequestDTO;
import com.mif.movieInsideForum.Module.Movie.dto.MovieResponseDTO;
import com.mif.movieInsideForum.DTO.ResponseWrapper;
import com.mif.movieInsideForum.Security.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/saved-movies")
@RequiredArgsConstructor
public class SavedMovieController {
    private final SavedMovieService savedMovieService;
    private final AuthenticationFacade authenticationFacade;

    @PostMapping("/{movieId}")
    public ResponseEntity<ResponseWrapper<Void>> saveMovie(@PathVariable ObjectId movieId) {
        ObjectId userId = authenticationFacade.getUser().getId();
        savedMovieService.saveMovie(userId, movieId);
        return ResponseEntity.ok(ResponseWrapper.<Void>builder()
                .status("success")
                .message("Movie has been saved")
                .build());
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity<ResponseWrapper<Void>> unsaveMovie(@PathVariable ObjectId movieId) {
        ObjectId userId = authenticationFacade.getUser().getId();
        savedMovieService.unsaveMovie(userId, movieId);
        return ResponseEntity.ok(ResponseWrapper.<Void>builder()
                .status("success")
                .message("Movie has been unsaved")
                .build());
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<ResponseWrapper<Boolean>> isMovieSaved(@PathVariable ObjectId movieId) {
        ObjectId userId = authenticationFacade.getUser().getId();
        boolean isSaved = savedMovieService.isMovieSaved(userId, movieId);
        return ResponseEntity.ok(ResponseWrapper.<Boolean>builder()
                .status("success")
                .data(isSaved)
                .message("Movie saved status")
                .build());
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<Slice<MovieResponseDTO>>> getSavedMovies(@PageableDefault(size = 10) Pageable pageable) {
        ObjectId userId = authenticationFacade.getUser().getId();
        Slice<MovieResponseDTO> savedMovies = savedMovieService.getSavedMovies(userId, pageable);
        return ResponseEntity.ok(ResponseWrapper.<Slice<MovieResponseDTO>>builder()
                .status("success")
                .data(savedMovies)
                .message("List of saved movies")
                .build());
    }

    @PostMapping("/batch-check")
    public ResponseEntity<ResponseWrapper<Map<String, Boolean>>> batchCheckSavedStatus(@RequestBody BatchCheckRequestDTO request) {
        ObjectId userId = authenticationFacade.getUser().getId();
        List<ObjectId> movieIds = request.getPostIds().stream().map(ObjectId::new).toList();
        Map<String, Boolean> savedStatus = savedMovieService.batchCheckSavedMoviesStatus(userId, movieIds);
        return ResponseEntity.ok(ResponseWrapper.<Map<String, Boolean>>builder()
                .status("success")
                .data(savedStatus)
                .message("Saved status of multiple movies")
                .build());
    }
}