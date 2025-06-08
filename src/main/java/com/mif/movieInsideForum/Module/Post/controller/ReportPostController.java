package com.mif.movieInsideForum.Module.Post.controller;

import com.mif.movieInsideForum.Collection.Field.ReportStatus;
import com.mif.movieInsideForum.DTO.ResponseWrapper;
import com.mif.movieInsideForum.Module.Group.GroupService;
import com.mif.movieInsideForum.Module.Post.entity.GroupPostReport;
import com.mif.movieInsideForum.Module.Post.entity.ReportPost;
import com.mif.movieInsideForum.Module.Post.service.ReportPostService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/group-posts")
@RequiredArgsConstructor
public class ReportPostController {

    private final ReportPostService reportPostService;
    private final GroupService groupService;

    @PostMapping("/{postId}/report")
    public ResponseEntity<ResponseWrapper<ReportPost>> reportPost(
            @PathVariable String postId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        String reason = request.get("reason");
        String reporterId = authentication.getName();

        GroupPostReport report = new GroupPostReport(reporterId, reason);
        reportPostService.createOrUpdateReport(postId, report);

        return ResponseEntity.ok(ResponseWrapper.<ReportPost>builder()
                .status("success")
                .message("Báo cáo bài viết thành công")
                .build());
    }

    @GetMapping("/{groupId}/reports")
    public ResponseEntity<ResponseWrapper<Page<ReportPost>>> getReportedPosts(
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

        Page<ReportPost> reports = reportPostService.getReportedPostsByGroup(
            groupId,
            status,
            PageRequest.of(page, size)
        );

        return ResponseEntity.ok(ResponseWrapper.<Page<ReportPost>>builder()
                .status("success")
                .data(reports)
                .message("Lấy danh sách báo cáo thành công")
                .build());
    }

    @PostMapping("/reports/{reportId}/handle")
    public ResponseEntity<ResponseWrapper<ReportPost>> handleReport(
            @PathVariable String reportId,
            @RequestParam String status,
            Authentication authentication) {
        
        ReportStatus newStatus = ReportStatus.valueOf(status.toUpperCase());

        ReportPost updatedReport = reportPostService.handleReport(reportId, newStatus);

        return ResponseEntity.ok(ResponseWrapper.<ReportPost>builder()
                .status("success")
                .data(updatedReport)
                .message("Xử lý báo cáo thành công")
                .build());
    }

    @GetMapping("/reports/{reportId}/analyze")
    public ResponseEntity<ResponseWrapper<Map<String, Object>>> analyzeReport(
            @PathVariable String reportId) {

        Map<String, Object> analysis = reportPostService.analyzeReportContent(reportId);

        return ResponseEntity.ok(ResponseWrapper.<Map<String, Object>>builder()
                .status("success")
                .data(analysis)
                .message("Phân tích báo cáo thành công")
                .build());
    }
} 