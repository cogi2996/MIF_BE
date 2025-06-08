package com.mif.movieInsideForum.Module.Movie;

import com.mif.movieInsideForum.DTO.DirectorRequestDTO;
import com.mif.movieInsideForum.DTO.DirectorResponseDTO;
import com.mif.movieInsideForum.DTO.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<DirectorResponseDTO>> createDirector(@RequestBody DirectorRequestDTO directorRequestDTO) {
        DirectorResponseDTO createdDirector = directorService.createDirector(directorRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.<DirectorResponseDTO>builder()
                        .status("success")
                        .data(createdDirector)
                        .message("Đạo diễn đã được tạo thành công")
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<DirectorResponseDTO>> getDirectorById(@PathVariable ObjectId id) {
        DirectorResponseDTO director = directorService.getDirectorById(id);
        if (director != null) {
            return ResponseEntity.ok(ResponseWrapper.<DirectorResponseDTO>builder()
                    .status("success")
                    .data(director)
                    .message("Thông tin đạo diễn")
                    .build());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseWrapper.<DirectorResponseDTO>builder()
                            .status("error")
                            .data(null)
                            .message("Không tìm thấy đạo diễn")
                            .build());
        }
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<List<DirectorResponseDTO>>> getAllDirectors() {
        List<DirectorResponseDTO> directors = directorService.getAllDirectors();
        return ResponseEntity.ok(ResponseWrapper.<List<DirectorResponseDTO>>builder()
                .status("success")
                .data(directors)
                .message("Danh sách tất cả đạo diễn")
                .build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseWrapper<DirectorResponseDTO>> updateDirector(@PathVariable ObjectId id, @RequestBody DirectorRequestDTO directorRequestDTO) {
        DirectorResponseDTO updatedDirector = directorService.updateDirector(id, directorRequestDTO);
        if (updatedDirector != null) {
            return ResponseEntity.ok(ResponseWrapper.<DirectorResponseDTO>builder()
                    .status("success")
                    .data(updatedDirector)
                    .message("Đạo diễn đã được cập nhật")
                    .build());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseWrapper.<DirectorResponseDTO>builder()
                            .status("error")
                            .data(null)
                            .message("Không tìm thấy đạo diễn để cập nhật")
                            .build());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Void>> deleteDirector(@PathVariable ObjectId id) {
        directorService.deleteDirector(id);
        return ResponseEntity.ok(ResponseWrapper.<Void>builder()
                .status("success")
                .data(null)
                .message("Đạo diễn đã được xóa")
                .build());
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseWrapper<Slice<DirectorResponseDTO>>> searchDirectorByName(@RequestParam String name, @PageableDefault(size = 5) Pageable pageable) {
        Slice<DirectorResponseDTO> directors = directorService.findByNameContainingIgnoreCase(name, pageable);
        return ResponseEntity.ok(ResponseWrapper.<Slice<DirectorResponseDTO>>builder()
                .status("success")
                .data(directors)
                .message("Kết quả tìm kiếm đạo diễn")
                .build());
    }
}