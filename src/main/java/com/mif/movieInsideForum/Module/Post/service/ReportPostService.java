package com.mif.movieInsideForum.Module.Post.service;

import com.mif.movieInsideForum.Collection.Field.ReportStatus;
import com.mif.movieInsideForum.Module.Post.entity.GroupPostReport;
import com.mif.movieInsideForum.Module.Post.entity.ReportPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface ReportPostService {
    // Tạo report mới hoặc thêm vào report hiện có
    ReportPost createOrUpdateReport(String postId, GroupPostReport report);
    
    // Lấy danh sách bài viết bị report trong group theo trạng thái
    Page<ReportPost> getReportedPostsByGroup(String groupId, ReportStatus status, Pageable pageable);
    
    // Xử lý report (reject hoặc block)
    ReportPost handleReport(String reportId, ReportStatus newStatus);

    // Phân tích nội dung report
    Map<String, Object> analyzeReportContent(String reportId);
} 