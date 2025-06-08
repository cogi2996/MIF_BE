package com.mif.movieInsideForum.Module.Movie;

import java.util.List;
import java.util.Map;

import com.mif.movieInsideForum.DTO.*;
import com.mif.movieInsideForum.Module.Movie.dto.MovieRequestDTO;
import com.mif.movieInsideForum.Module.Movie.dto.MovieResponseDTO;
import com.mif.movieInsideForum.Module.Movie.dto.MovieUpdateDTO;
import com.mif.movieInsideForum.Module.Movie.dto.MovieSentimentStatsDTO;
import com.mif.movieInsideForum.Module.MovieRating.dto.SentimentStatsDTO;
import com.mif.movieInsideForum.Module.MovieRating.dto.MovieSentimentExtremeDTO;
import com.mif.movieInsideForum.Module.MovieRating.dto.MovieSentimentPercentageDTO;
import com.mif.movieInsideForum.Module.MovieRating.service.MovieRatingsService;
import com.mif.movieInsideForum.Collection.MovieCategory;
import com.mif.movieInsideForum.Collection.Director;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.mif.movieInsideForum.Module.MovieRating.repository.MovieRatingsRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.io.ByteArrayOutputStream;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;
    private final MovieRatingsService movieRatingsService;
    private final MovieRepository movieRepository;
    private final MovieRatingsRepository movieRatingsRepository;

    @GetMapping("/sentiment-stats")
    public ResponseEntity<ResponseWrapper<MovieSentimentStatsDTO>> getMovieSentimentStats() {
        // Lấy thống kê tổng thể
        SentimentStatsDTO overallStats = movieRatingsRepository.getOverallSentimentStats()
            .orElseThrow(() -> new RuntimeException("Không thể lấy thống kê cảm xúc"));

        // Lấy phim có cảm xúc cực đoan nhất
        MovieSentimentExtremeDTO extremeStats = movieRatingsRepository.findMoviesWithExtremeSentiments()
            .orElseThrow(() -> new RuntimeException("Không thể lấy thông tin phim có cảm xúc cực đoan"));

        // Lấy thông tin chi tiết của các phim có cảm xúc cực đoan
        ObjectId mostPositiveMovieId = null;
        ObjectId mostNegativeMovieId = null;
        double mostPositivePercentage = 0.0;
        double mostNegativePercentage = 0.0;

        if (!extremeStats.getMostPositive().isEmpty()) {
            MovieSentimentPercentageDTO mostPositive = extremeStats.getMostPositive().get(0);
            mostPositiveMovieId = mostPositive.get_id();
            mostPositivePercentage = mostPositive.getPositivePercentage();
        }

        if (!extremeStats.getMostNegative().isEmpty()) {
            MovieSentimentPercentageDTO mostNegative = extremeStats.getMostNegative().get(0);
            mostNegativeMovieId = mostNegative.get_id();
            mostNegativePercentage = mostNegative.getNegativePercentage();
        }

        // Lấy tên của các phim
        String mostPositiveMovieTitle = null;
        String mostNegativeMovieTitle = null;

        if (mostPositiveMovieId != null) {
            Movie mostPositiveMovie = movieRepository.findById(mostPositiveMovieId).orElse(null);
            if (mostPositiveMovie != null) {
                mostPositiveMovieTitle = mostPositiveMovie.getTitle();
            }
        }

        if (mostNegativeMovieId != null) {
            Movie mostNegativeMovie = movieRepository.findById(mostNegativeMovieId).orElse(null);
            if (mostNegativeMovie != null) {
                mostNegativeMovieTitle = mostNegativeMovie.getTitle();
            }
        }

        // Tạo response
        MovieSentimentStatsDTO stats = MovieSentimentStatsDTO.builder()
            .totalComments(overallStats.getTotalComments().intValue())
            .positivePercentage(overallStats.getPositiveCount() * 100.0 / overallStats.getTotalComments())
            .negativePercentage(overallStats.getNegativeCount() * 100.0 / overallStats.getTotalComments())
            .neutralPercentage(overallStats.getNeutralCount() * 100.0 / overallStats.getTotalComments())
            .mostPositiveMovie(mostPositiveMovieTitle)
            .mostPositivePercentage(mostPositivePercentage)
            .mostNegativeMovie(mostNegativeMovieTitle)
            .mostNegativePercentage(mostNegativePercentage)
            .lastUpdated(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")))
            .build();
            
        return ResponseEntity.ok(ResponseWrapper.<MovieSentimentStatsDTO>builder()
            .status("success")
            .message("Thống kê cảm xúc của các bộ phim")
            .data(stats)
            .build());
    }

    @GetMapping("/newest")
    public ResponseEntity<ResponseWrapper<Slice<MovieResponseDTO>>> getNewestMovie(@PageableDefault(size = 4) Pageable pageable) {
        Slice<MovieResponseDTO> sliceMovie = movieService.getLatestMovie(pageable);
        return ResponseEntity.ok(ResponseWrapper.<Slice<MovieResponseDTO>>builder()
                .status("success")
                .message("Danh sách phim mới nhất")
                .data(sliceMovie)
                .build());
    }

    @GetMapping("/random")
    public ResponseEntity<ResponseWrapper<List<MovieResponseDTO>>> get4RandomMovies() {
        List<MovieResponseDTO> movies = movieService.get4RandomMovies();
        return ResponseEntity.ok(ResponseWrapper.<List<MovieResponseDTO>>builder()
                .status("success")
                .message("Danh sách 4 phim ngẫu nhiên")
                .data(movies)
                .build());
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<MovieResponseDTO>> createMovie(@RequestBody MovieRequestDTO movieDTO) {
        MovieResponseDTO createdMovie = movieService.createMovie(movieDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.<MovieResponseDTO>builder()
                        .status("success")
                        .message("Phim đã được tạo thành công")
                        .data(createdMovie)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<MovieResponseDTO>> getMovieById(@PathVariable ObjectId id) {
        MovieResponseDTO movieResponseDTO = movieService.getMovieById(id);
        if (movieResponseDTO != null) {
            return ResponseEntity.ok(ResponseWrapper.<MovieResponseDTO>builder()
                    .status("success")
                    .message("Thông tin phim")
                    .data(movieResponseDTO)
                    .build());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseWrapper.<MovieResponseDTO>builder()
                            .status("error")
                            .message("Không tìm thấy phim")
                            .build());
        }
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<?>> getAllMovies(
            @RequestParam(required = false) ObjectId categoryId,
            @RequestParam(required = false, defaultValue = "false") boolean pageView,
            @PageableDefault(size = 4) Pageable pageable) {
        if (pageView) {
            System.out.println("Admin view");
            Page<MovieResponseDTO> movieDTOs = categoryId != null 
                ? movieService.findMoviesByCategoryAsPage(categoryId, pageable) 
                : movieService.getAllMoviesAsPage(pageable);
            return ResponseEntity.ok(ResponseWrapper.<Page<MovieResponseDTO>>builder()
                    .status("success")
                    .message("Admin view of all movies")
                    .data(movieDTOs)
                    .build());
        } else {
            Slice<MovieResponseDTO> movieDTOs = categoryId != null 
                ? movieService.findMoviesByCategory(categoryId, pageable) 
                : movieService.getAllMovies(pageable);
            return ResponseEntity.ok(ResponseWrapper.<Slice<MovieResponseDTO>>builder()
                    .status("success")
                    .message("User view of all movies")
                    .data(movieDTOs)
                    .build());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Void>> updateMovie(@PathVariable ObjectId id, @RequestBody MovieUpdateDTO movieUpdateDTO) {
        boolean isUpdated = movieService.updateMovie(id, movieUpdateDTO);
        if (isUpdated) {
            return ResponseEntity.ok(ResponseWrapper.<Void>builder()
                    .status("success")
                    .message("Phim đã được cập nhật")
                    .build());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseWrapper.<Void>builder()
                            .status("error")
                            .message("Không tìm thấy phim để cập nhật")
                            .build());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Void>> deleteMovieById(@PathVariable ObjectId id) {
        movieService.deleteMovie(id);
        return ResponseEntity.ok(ResponseWrapper.<Void>builder()
                .status("success")
                .message("Phim đã được xóa")
                .build());
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseWrapper<Slice<MovieResponseDTO>>> searchMoviesByTitle(@RequestParam String title, @PageableDefault(size = 4) Pageable pageable) {
        Slice<MovieResponseDTO> movies = movieService.findMoviesByTitle(title, pageable);
        return ResponseEntity.ok(ResponseWrapper.<Slice<MovieResponseDTO>>builder()
                .status("success")
                .message("Kết quả tìm kiếm phim")
                .data(movies)
                .build());
    }

    @PostMapping("/{id}/cast")
    public ResponseEntity<ResponseWrapper<List<ActorResponseDTO>>> addCast(@PathVariable ObjectId id, @RequestBody List<ObjectId> actorIds) {
        List<ActorResponseDTO> addedActors = movieService.addCast(id, actorIds);
        return ResponseEntity.ok(ResponseWrapper.<List<ActorResponseDTO>>builder()
                .status("success")
                .message("Diễn viên đã được thêm vào phim")
                .data(addedActors)
                .build());
    }

    @DeleteMapping("/{id}/cast")
    public ResponseEntity<ResponseWrapper<Boolean>> removeCast(@PathVariable ObjectId id, @RequestBody List<ObjectId> actorIds) {
        Boolean removed = movieService.removeCast(id, actorIds);
        return ResponseEntity.ok(ResponseWrapper.<Boolean>builder()
                .status("success")
                .message("Diễn viên đã được xóa khỏi phim")
                .data(removed)
                .build());
    }

    @GetMapping("/{movieId}/cast")
    public ResponseEntity<ResponseWrapper<List<ActorResponseDTO>>> getMovieCast(@PathVariable ObjectId movieId) {
        List<ActorResponseDTO> cast = movieService.getMovieCast(movieId);
        return ResponseEntity.ok(ResponseWrapper.<List<ActorResponseDTO>>builder()
                .status("success")
                .message("Danh sách diễn viên của phim")
                .data(cast)
                .build());
    }

    @GetMapping("/{movieId}/ratings")
    public ResponseEntity<ResponseWrapper<Slice<MovieRatingsResponseDTO>>> getAllRatingsByMovieId(@PathVariable ObjectId movieId, Pageable pageable) {
        Slice<MovieRatingsResponseDTO> ratings = movieRatingsService.getAllRatingsByMovieId(movieId, pageable);
        return ResponseEntity.ok(ResponseWrapper.<Slice<MovieRatingsResponseDTO>>builder()
                .status("success")
                .message("Danh sách đánh giá phim")
                .data(ratings)
                .build());
    }

    @GetMapping("/{id}/images")
    public ResponseEntity<ResponseWrapper<List<String>>> getMovieImages(@PathVariable ObjectId id) {
        List<String> images = movieService.getMovieImages(id);
        return ResponseEntity.ok(ResponseWrapper.<List<String>>builder()
                .status("success")
                .message("Danh sách ảnh của phim")
                .data(images)
                .build());
    }

    @PutMapping("/{id}/images")
    public ResponseEntity<ResponseWrapper<Void>> updateMovieImages(@PathVariable ObjectId id, @RequestBody Map<String,List<String>> newImages) {
        movieService.updateMovieImages(id, newImages.get("url"));
        return ResponseEntity.ok(ResponseWrapper.<Void>builder()
                .status("success")
                .message("Danh sách ảnh đã được cập nhật")
                .build());
    }

    @GetMapping("/export")
    // allow all role
    @PreAuthorize("permitAll()")
    public ResponseEntity<Resource> exportMoviesToExcel() {
        try {
            List<Movie> movies = movieRepository.findAll();
            
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Movies");
            
            // Tạo header
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Tiêu đề", "Mô tả", "Thể loại", "Ngày phát hành", "Thời lượng"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }
            
            // Thêm dữ liệu
            int rowNum = 1;
            for (Movie movie : movies) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(movie.getId().toString());
                row.createCell(1).setCellValue(movie.getTitle());
                row.createCell(2).setCellValue(movie.getDescription());
                row.createCell(3).setCellValue(movie.getGenre() != null ? 
                    movie.getGenre().stream()
                        .map(MovieCategory::getCategoryName)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("") : "");
                row.createCell(4).setCellValue(movie.getReleaseDate() != null ? movie.getReleaseDate().toString() : "");
                row.createCell(5).setCellValue(movie.getDuration() != null ? movie.getDuration().toString() : "");
            }
            
            // Tự động điều chỉnh độ rộng cột
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Tạo file Excel
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            
            // Tạo tên file với timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "movies_" + timestamp + ".xlsx";
            
            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .contentLength(resource.contentLength())
                    .body(resource);
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}