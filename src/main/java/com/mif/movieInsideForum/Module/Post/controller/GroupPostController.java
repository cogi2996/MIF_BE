package com.mif.movieInsideForum.Module.Post.controller;

import com.mif.movieInsideForum.DTO.Response.NewestPostResponseDTO;
import com.mif.movieInsideForum.Module.Post.DTO.GroupPostRequestDTO;
import com.mif.movieInsideForum.Module.Post.DTO.GroupPostResponseDTO;
import com.mif.movieInsideForum.Module.Post.service.GroupPostRatingsService;
import com.mif.movieInsideForum.Module.Post.service.GroupPostService;
import com.mif.movieInsideForum.Module.Group.GroupService;
import com.mif.movieInsideForum.DTO.ProfilePostDTO;
import com.mif.movieInsideForum.Messaging.Producer.NotificationProducer;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mif.movieInsideForum.DTO.ResponseWrapper;
import com.mif.movieInsideForum.DTO.UserDTO;
import com.mif.movieInsideForum.Security.AuthenticationFacade;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import java.time.Year;
import java.util.Map;
import com.mif.movieInsideForum.Collection.Field.ReportStatus;
import com.mif.movieInsideForum.Module.Post.entity.ReportPost;
import com.mif.movieInsideForum.Module.Post.service.ReportPostService;
import org.springframework.data.domain.PageRequest;

@RestController
@RequestMapping("/group-posts")
@RequiredArgsConstructor
public class GroupPostController {
    private final GroupPostService groupPostService;
    private final AuthenticationFacade authenticationFacade;
    private final GroupPostRatingsService groupPostRatingsService;
    private final GroupService groupService;
    private final NotificationProducer notificationProducer;
    private final ReportPostService reportPostService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<GroupPostResponseDTO>> createPost(@RequestBody GroupPostRequestDTO groupPostRequestDTO) {
        groupPostRequestDTO.setOwner(UserDTO.builder().id(authenticationFacade.getUser().getId()).build());
        GroupPostResponseDTO createdPost = groupPostService.createPost(groupPostRequestDTO);
        return ResponseEntity.ok(ResponseWrapper.<GroupPostResponseDTO>builder()
                .status("success")
                .message("Bài đăng đã được tạo thành công")
                .data(createdPost)
                .build());
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ResponseWrapper<GroupPostResponseDTO>> getPostById(@PathVariable ObjectId postId) {
        GroupPostResponseDTO post = groupPostService.getPostById(postId);
        return ResponseEntity.ok(ResponseWrapper.<GroupPostResponseDTO>builder()
                .status("success")
                .message("Thông tin bài đăng")
                .data(post)
                .build());
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<?>> getAllPosts(
            @RequestParam(required = false, defaultValue = "false") boolean pageView,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        if (pageView) {
            Page<GroupPostResponseDTO> posts = groupPostService.findAllAsPage(pageable);
            return ResponseEntity.ok(ResponseWrapper.<Page<GroupPostResponseDTO>>builder()
                    .status("success")
                    .message("Danh sách bài đăng (Page view)")
                    .data(posts)
                    .build());
        } else {
            Slice<NewestPostResponseDTO> posts = groupPostService.getAllPosts(pageable);
            return ResponseEntity.ok(ResponseWrapper.<Slice<NewestPostResponseDTO>>builder()
                    .status("success")
                    .message("Danh sách bài đăng (Slice view)")
                    .data(posts)
                    .build());
        }
    }

    @GetMapping("/featured")
    public ResponseEntity<ResponseWrapper<Slice<GroupPostResponseDTO>>> getFeaturedPosts(
            @PageableDefault(size = 5) Pageable pageable, 
            @RequestParam ObjectId groupId) {
        Slice<GroupPostResponseDTO> featuredPosts = groupPostService.getFeaturedPosts(pageable, groupId);
        return ResponseEntity.ok(ResponseWrapper.<Slice<GroupPostResponseDTO>>builder()
                .status("success")
                .message("Danh sách bài đăng nổi bật")
                .data(featuredPosts)
                .build());
    }

    @PutMapping("/{postId}")
    public ResponseEntity<ResponseWrapper<GroupPostResponseDTO>> updatePost(@PathVariable ObjectId postId, @RequestBody GroupPostRequestDTO groupPostRequestDTO) {
        GroupPostResponseDTO updatedPost = groupPostService.updatePost(postId, groupPostRequestDTO);
        return ResponseEntity.ok(ResponseWrapper.<GroupPostResponseDTO>builder()
                .status("success")
                .message("Bài đăng đã được cập nhật")
                .data(updatedPost)
                .build());
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ResponseWrapper<Void>> deletePost(@PathVariable ObjectId postId) {
        groupPostService.deletePost(postId);
        return ResponseEntity.ok(ResponseWrapper.<Void>builder()
                .status("success")
                .message("Bài đăng đã được xóa")
                .build());
    }

    @PostMapping("/{postId}/upvote")
    public ResponseEntity<ResponseWrapper<Void>> upvotePost(@PathVariable ObjectId postId) {
        ObjectId userId = authenticationFacade.getUser().getId();
        groupPostRatingsService.upVote(postId, userId);
        // update post rating


        return ResponseEntity.ok(ResponseWrapper.<Void>builder()
                .status("success")
                .message("Đã upvote bài đăng")
                .build());
    }




    @PostMapping("/{postId}/downvote")
    public ResponseEntity<ResponseWrapper<Void>> downVotePost(@PathVariable ObjectId postId) {
        ObjectId userId = authenticationFacade.getUser().getId();
        groupPostRatingsService.downVote(postId, userId);
        return ResponseEntity.ok(ResponseWrapper.<Void>builder()
                .status("success")
                .message("Đã downvote bài đăng")
                .build());
    }

    @DeleteMapping("/{postId}/vote")
    public ResponseEntity<ResponseWrapper<Void>> removeVote(@PathVariable ObjectId postId) {
        ObjectId userId = authenticationFacade.getUser().getId();
        groupPostRatingsService.removeVote(postId, userId);
        return ResponseEntity.ok(ResponseWrapper.<Void>builder()
                .status("success")
                .message("Đã xóa vote")
                .build());
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<ResponseWrapper<Slice<ProfilePostDTO>>> getProfilePosts(
            @PathVariable ObjectId userId,
            @PageableDefault(size = 5) Pageable pageable,
            @RequestParam(required = false, defaultValue = "false") boolean includeBlocked) {
        Slice<ProfilePostDTO> posts = groupPostService.getProfilePost(userId, pageable, includeBlocked);
        return ResponseEntity.ok(ResponseWrapper.<Slice<ProfilePostDTO>>builder()
                .status("success")
                .message("Danh sách bài đăng của người dùng")
                .data(posts)
                .build());
    }

    @GetMapping("/statistics/monthly")
    public ResponseEntity<ResponseWrapper<Map<Integer, Integer>>> getPostStatisticsByMonth(@RequestParam(value = "year", required = false) Integer year) {
        int queryYear = (year != null) ? year : Year.now().getValue();
        Map<Integer, Integer> stats = groupPostService.countPostsByMonth(queryYear);
        return ResponseEntity.ok(ResponseWrapper.<Map<Integer, Integer>>builder()
                .status("success")
                .message("Thống kê số lượng bài viết theo từng tháng năm " + queryYear)
                .data(stats)
                .build());
    }

    @GetMapping("/{groupId}/reported-posts")
    public ResponseEntity<ResponseWrapper<Page<ReportPost>>> getReportedPostsByGroup(
            @PathVariable String groupId,
            @RequestParam(defaultValue = "PENDING") ReportStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        if (!groupService.isUserOwnerOfGroup(new ObjectId(groupId), new ObjectId(authentication.getName()))) {
            return ResponseEntity.status(403).body(ResponseWrapper.<Page<ReportPost>>builder()
                    .status("error")
                    .message("Không có quyền truy cập. Chỉ trưởng nhóm mới có thể xem báo cáo")
                    .build());
        }
        var reports = reportPostService.getReportedPostsByGroup(groupId, status, PageRequest.of(page, size));
        return ResponseEntity.ok(ResponseWrapper.<Page<ReportPost>>builder()
                .status("success")
                .data(reports)
                .message("Lấy danh sách bài viết bị report thành công")
                .build());
    }
}