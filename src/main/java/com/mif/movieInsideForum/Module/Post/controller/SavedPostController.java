package com.mif.movieInsideForum.Module.Post.controller;

import com.mif.movieInsideForum.DTO.BatchCheckRequestDTO;
import com.mif.movieInsideForum.DTO.ResponseWrapper;
import com.mif.movieInsideForum.Module.Post.DTO.GroupPostResponseDTO;
import com.mif.movieInsideForum.Module.Post.service.SavedPostService;
import com.mif.movieInsideForum.Security.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/saved-posts")
@RequiredArgsConstructor
public class SavedPostController {
    private final SavedPostService savedPostService;
    private final AuthenticationFacade authenticationFacade;

    @PostMapping("/{postId}")
    public ResponseEntity<ResponseWrapper<Void>> savePost(@PathVariable ObjectId postId) {
        ObjectId userId = authenticationFacade.getUser().getId();
        savedPostService.savePost(userId, postId);
        return ResponseEntity.ok(ResponseWrapper.<Void>builder()
                .status("success")
                .message("Bài viết đã được lưu")
                .build());
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ResponseWrapper<Void>> unsavePost(@PathVariable ObjectId postId) {
        ObjectId userId = authenticationFacade.getUser().getId();
        savedPostService.unsavePost(userId, postId);
        return ResponseEntity.ok(ResponseWrapper.<Void>builder()
                .status("success")
                .message("Bài viết đã được bỏ lưu")
                .build());
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ResponseWrapper<Boolean>> isPostSaved(@PathVariable ObjectId postId) {
        ObjectId userId = authenticationFacade.getUser().getId();
        boolean isSaved = savedPostService.isPostSaved(userId, postId);
        return ResponseEntity.ok(ResponseWrapper.<Boolean>builder()
                .status("success")
                .data(isSaved)
                .message("Trạng thái lưu của bài viết")
                .build());
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<Slice<GroupPostResponseDTO>>> getSavedPosts(@PageableDefault(size = 10) Pageable pageable) {
        ObjectId userId = authenticationFacade.getUser().getId();
        Slice<GroupPostResponseDTO> savedPosts = savedPostService.getSavedPosts(userId, pageable);
        return ResponseEntity.ok(ResponseWrapper.<Slice<GroupPostResponseDTO>>builder()
                .status("success")
                .data(savedPosts)
                .message("Danh sách bài viết đã lưu")
                .build());
    }

    @PostMapping("/batch-check")
    public ResponseEntity<ResponseWrapper<Map<String, Boolean>>> batchCheckSavedStatus(@RequestBody BatchCheckRequestDTO request) {
        ObjectId userId = authenticationFacade.getUser().getId();
        List<ObjectId> postIds = request.getPostIds().stream().map(ObjectId::new).toList();
        Map<String, Boolean> savedStatus = savedPostService.batchCheckSavedStatus(userId, postIds);
        return ResponseEntity.ok(ResponseWrapper.<Map<String, Boolean>>builder()
                .status("success")
                .data(savedStatus)
                .message("Trạng thái lưu của các bài viết")
                .build());
    }
}