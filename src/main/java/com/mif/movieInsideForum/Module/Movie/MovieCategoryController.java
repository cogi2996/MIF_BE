package com.mif.movieInsideForum.Module.Movie;

import com.mif.movieInsideForum.Collection.MovieCategory;
import com.mif.movieInsideForum.DTO.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MovieCategoryController {
    private final MovieCategoryService movieCategoryService;

    @GetMapping("/movies-category")
    public ResponseEntity<ResponseWrapper<List<MovieCategory>>> getNewsCategories(Authentication authentication) {
        List<MovieCategory> categories = movieCategoryService.getNewsCategories();
        return ResponseEntity.ok(ResponseWrapper.<List<MovieCategory>>builder()
                .status("success")
                .message("Danh sách thể loại phim")
                .data(categories)
                .build());
    }

    @GetMapping("/movies-category/{id}")
    public ResponseEntity<ResponseWrapper<MovieCategory>> getNewsCategoryById(@PathVariable ObjectId id) {
        MovieCategory category = movieCategoryService.getNewsCategory(id);
        return ResponseEntity.ok(ResponseWrapper.<MovieCategory>builder()
                .status("success")
                .message("Thông tin thể loại phim")
                .data(category)
                .build());
    }

    @PostMapping("/movies-category")
    public ResponseEntity<ResponseWrapper<MovieCategory>> createNewsCategory(@RequestBody MovieCategory movieCategory) {
        MovieCategory createdCategory = movieCategoryService.createNewsCategory(movieCategory);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.<MovieCategory>builder()
                        .status("success")
                        .message("Thể loại phim đã được tạo thành công")
                        .data(createdCategory)
                        .build());
    }

    @PutMapping("/movies-category/{id}")
    public ResponseEntity<ResponseWrapper<MovieCategory>> updateNewsCategory(@PathVariable ObjectId id, @RequestBody MovieCategory movieCategory) {
        MovieCategory updatedCategory = movieCategoryService.updateNewsCategory(id, movieCategory);
        return ResponseEntity.ok(ResponseWrapper.<MovieCategory>builder()
                .status("success")
                .message("Thể loại phim đã được cập nhật")
                .data(updatedCategory)
                .build());
    }

    @DeleteMapping("/movies-category/{id}")
    public ResponseEntity<ResponseWrapper<Void>> deleteNewsCategory(@PathVariable ObjectId id) {
        movieCategoryService.deleteNewsCategory(id);
        return ResponseEntity.ok(ResponseWrapper.<Void>builder()
                .status("success")
                .message("Thể loại phim đã được xóa")
                .build());
    }
}