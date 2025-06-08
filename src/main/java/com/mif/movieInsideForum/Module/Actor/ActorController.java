package com.mif.movieInsideForum.Module.Actor;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mif.movieInsideForum.DTO.ActorRequestDTO;
import com.mif.movieInsideForum.DTO.ActorResponseDTO;
import com.mif.movieInsideForum.Module.Movie.dto.MovieResponseDTO;
import com.mif.movieInsideForum.DTO.ResponseWrapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/actors")
@RequiredArgsConstructor
public class ActorController {
    private final ActorService actorService;

    @GetMapping("/search")
    public ResponseEntity<ResponseWrapper<Slice<ActorResponseDTO>>> searchActorsByTitle(@RequestParam String title, @PageableDefault(size = 4) Pageable pageable) {
        Slice<ActorResponseDTO> actors = actorService.findActorsByTitle(title, pageable);
        return ResponseEntity.ok(ResponseWrapper.<Slice<ActorResponseDTO>>builder()
                .status("success")
                .message("Kết quả tìm kiếm diễn viên")
                .data(actors)
                .build());
    }

    @GetMapping("/top")
    public ResponseEntity<ResponseWrapper<Slice<ActorResponseDTO>>> getTopActors(@RequestParam int page, @RequestParam int size) {
        Slice<ActorResponseDTO> topActors = actorService.getTopActors(page, size);
        return ResponseEntity.ok(ResponseWrapper.<Slice<ActorResponseDTO>>builder()
                .status("success")
                .message("Danh sách diễn viên hàng đầu")
                .data(topActors)
                .build());
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<ActorResponseDTO>> createActor(@RequestBody ActorRequestDTO actorRequestDTO) {
        ActorResponseDTO createdActor = actorService.createActor(actorRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.<ActorResponseDTO>builder()
                        .status("success")
                        .message("Diễn viên đã được tạo")
                        .data(createdActor)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<ActorResponseDTO>> getActorById(@PathVariable ObjectId id) {
        ActorResponseDTO actor = actorService.getActorById(id);
        if (actor != null) {
            return ResponseEntity.ok(ResponseWrapper.<ActorResponseDTO>builder()
                    .status("success")
                    .data(actor)
                    .message("Thông tin diễn viên")
                    .build());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseWrapper.<ActorResponseDTO>builder()
                            .status("error")
                            .data(null)
                            .message("Không tìm thấy diễn viên")
                            .build());
        }
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<?>> findAllWithPag(
            @RequestParam(required = false, defaultValue = "false") boolean pageView,
            @PageableDefault(size = 5) Pageable pageable) {
        if (pageView) {
            Page<ActorResponseDTO> actorDTOs = actorService.getAllActorsAsPage(pageable);
            return ResponseEntity.ok(ResponseWrapper.<Page<ActorResponseDTO>>builder()
                    .status("success")
                    .message("Admin view of all actors")
                    .data(actorDTOs)
                    .build());
        } else {
            Slice<ActorResponseDTO> actors = actorService.findAllWithPag(pageable);
            return ResponseEntity.ok(ResponseWrapper.<Slice<ActorResponseDTO>>builder()
                    .status("success")
                    .message("User view of all actors")
                    .data(actors)
                    .build());
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseWrapper<ActorResponseDTO>> updateActor(@PathVariable ObjectId id, @RequestBody ActorRequestDTO actorRequestDTO) {
        ActorResponseDTO updatedActor = actorService.updateActor(id, actorRequestDTO);
        if (updatedActor != null) {
            return ResponseEntity.ok(ResponseWrapper.<ActorResponseDTO>builder()
                    .status("success")
                    .data(updatedActor)
                    .message("Diễn viên đã được cập nhật")
                    .build());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseWrapper.<ActorResponseDTO>builder()
                            .status("error")
                            .data(null)
                            .message("Không tìm thấy diễn viên để cập nhật")
                            .build());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Void>> deleteActor(@PathVariable ObjectId id) {
        actorService.deleteActor(id);
        return ResponseEntity.ok(ResponseWrapper.<Void>builder()
                .status("success")
                .message("Diễn viên đã được xóa")
                .build());
    }

    @GetMapping("/{actorId}/filmography")
    public ResponseEntity<ResponseWrapper<List<MovieResponseDTO>>> getActorFilmography(@PathVariable ObjectId actorId) {
        List<MovieResponseDTO> filmography = actorService.getActorFilmography(actorId);
        return ResponseEntity.ok(ResponseWrapper.<List<MovieResponseDTO>>builder()
                .status("success")
                .message("Danh sách phim của diễn viên")
                .data(filmography)
                .build());
    }
}