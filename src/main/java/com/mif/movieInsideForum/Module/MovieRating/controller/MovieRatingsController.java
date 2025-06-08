package com.mif.movieInsideForum.Module.MovieRating.controller;

import com.mif.movieInsideForum.DTO.MovieRatingsRequestDTO;
import com.mif.movieInsideForum.DTO.MovieRatingsResponseDTO;
import com.mif.movieInsideForum.DTO.ResponseWrapper;
import com.mif.movieInsideForum.Module.MovieRating.service.MovieRatingsService;
import com.mif.movieInsideForum.Module.MovieRating.dto.MovieSentimentStatsDTO;
import com.mif.movieInsideForum.Security.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.Map;

@RestController
@RequestMapping("/movie-ratings")
@RequiredArgsConstructor
public class MovieRatingsController {
    private final MovieRatingsService movieRatingsService;
    private final AuthenticationFacade authenticationFacade;

    @PostMapping
    public ResponseEntity<ResponseWrapper<MovieRatingsResponseDTO>> rateMovie(@RequestBody MovieRatingsRequestDTO movieRatingsRequestDTO) {
        ObjectId userId = authenticationFacade.getUser().getId();
        MovieRatingsResponseDTO response = movieRatingsService.rateMovie(userId, movieRatingsRequestDTO);
        return ResponseEntity.ok(ResponseWrapper.<MovieRatingsResponseDTO>builder()
                .status("success")
                .message("Đánh giá phim thành công")
                .data(response)
                .build());
    }
    @DeleteMapping("/{movieId}")
    public ResponseEntity<ResponseWrapper<Void>> removeRating(@PathVariable ObjectId movieId) {
        ObjectId userId = authenticationFacade.getUser().getId();
        movieRatingsService.removeRating(userId, movieId);
        return ResponseEntity.ok(ResponseWrapper.<Void>builder()
                .status("success")
                .message("Xóa đánh giá phim thành công")
                .build());
    }

    @GetMapping("/{movieId}/sentiment-stats")
    public ResponseEntity<ResponseWrapper<MovieSentimentStatsDTO>> getMovieSentimentStatsDetail(@PathVariable ObjectId movieId) {
        MovieSentimentStatsDTO stats = movieRatingsService.getMovieSentimentStats(movieId);
        return ResponseEntity.ok(ResponseWrapper.<MovieSentimentStatsDTO>builder()
                .status("success")
                .message("Lấy thống kê cảm xúc thành công")
                .data(stats)
                .build());
    }

    @GetMapping("/statistics/monthly")
    public ResponseEntity<ResponseWrapper<Map<Integer, Integer>>> getRatingStatisticsByMonth(@RequestParam(value = "year", required = false) Integer year) {
        int queryYear = (year != null) ? year : Year.now().getValue();
        Map<Integer, Integer> stats = movieRatingsService.countRatingsByMonth(queryYear);
        return ResponseEntity.ok(ResponseWrapper.<Map<Integer, Integer>>builder()
                .status("success")
                .message("Thống kê số lượng rating theo từng tháng năm " + queryYear)
                .data(stats)
                .build());
    }
}