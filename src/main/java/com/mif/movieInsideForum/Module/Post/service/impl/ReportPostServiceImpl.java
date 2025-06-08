package com.mif.movieInsideForum.Module.Post.service.impl;

import com.mif.movieInsideForum.Collection.Field.ReportStatus;
import com.mif.movieInsideForum.Module.Post.entity.GroupPost;
import com.mif.movieInsideForum.Collection.Notification.Notification;
import com.mif.movieInsideForum.Collection.Notification.NotificationType;
import com.mif.movieInsideForum.Messaging.Producer.NotificationProducer;
import com.mif.movieInsideForum.Module.Post.entity.GroupPostReport;
import com.mif.movieInsideForum.Module.Post.entity.ReportPost;
import com.mif.movieInsideForum.Module.Post.repository.ReportPostRepository;
import com.mif.movieInsideForum.Module.Post.repository.GroupPostRepository;
import com.mif.movieInsideForum.Module.Post.service.ReportPostService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportPostServiceImpl implements ReportPostService {
    
    private final ReportPostRepository reportPostRepository;
    private final GroupPostRepository groupPostRepository;
    private final RestTemplate restTemplate;
    private final MongoTemplate mongoTemplate;
    private final NotificationProducer notificationProducer;
    @Override
    @Transactional
    public ReportPost createOrUpdateReport(String postId, GroupPostReport report) {
        Optional<ReportPost> existingReport = reportPostRepository.findByPostId(postId);
        
        if (existingReport.isPresent()) {
            ReportPost reportPost = existingReport.get();
            reportPost.getGroupReports().add(report);
            reportPost.setReportCount(reportPost.getReportCount() + 1);
            reportPost.setUpdatedAt(new Date());    
            return reportPostRepository.save(reportPost);
        } else {
            GroupPost groupPost = groupPostRepository.findById(new ObjectId(postId))
                    .orElseThrow(() -> new RuntimeException("Group not found"));
            
            ReportPost newReportPost = ReportPost.builder()
                    .postId(postId)
                    .groupId(groupPost.getGroup().getId().toString())
                    .ownerId(groupPost.getOwner().getId().toString())
                    .ownerUsername(groupPost.getOwner().getUsername())
                    .status(ReportStatus.PENDING)
                    .reportCount(1)
                    .createdAt(new Date())
                    .build();
            newReportPost.getGroupReports().add(report);
            return reportPostRepository.save(newReportPost);
        }
    }
    
    @Override
    public Page<ReportPost> getReportedPostsByGroup(String groupId, ReportStatus status, Pageable pageable) {
        return reportPostRepository.findByGroupIdAndStatus(groupId, status, pageable);
    }
    
    @Override
    @Transactional
    public ReportPost handleReport(String reportId, ReportStatus newStatus) {
        ReportPost reportPost = reportPostRepository.findById(new ObjectId(reportId))
                .orElseThrow(() -> new RuntimeException("Report not found"));
        
        reportPost.setStatus(newStatus);
        reportPost.setUpdatedAt(new Date());

        if(newStatus == ReportStatus.BLOCKED) {
            // Block bài viết
            Query query = new Query(Criteria.where("id").is(reportPost.getPostId()));
            Update update = new Update().set("isBlock", true);
            mongoTemplate.updateMulti(query, update, GroupPost.class);

            // Gửi thông báo cho chủ bài viết
            notificationProducer.sendNotification(
                Notification.builder()
                    .groupPostId(new ObjectId(reportPost.getPostId()))
                    .message("Bài viết của bạn đã bị chặn do vi phạm nội quy nhóm")
                    .receiverId(new ObjectId(reportPost.getOwnerId()))
                    .groupId(new ObjectId(reportPost.getGroupId()))
                    .type(NotificationType.POST_BLOCKED)
                    .build()
            );
        } else if(newStatus == ReportStatus.REJECTED) {
            // Gửi thông báo cho tất cả người report
            for (GroupPostReport report : reportPost.getGroupReports()) {
                notificationProducer.sendNotification(
                    Notification.builder()
                        .groupPostId(new ObjectId(reportPost.getPostId()))
                        .message("Báo cáo của bạn về bài viết đã bị từ chối")
                        .receiverId(new ObjectId(report.getReporterId()))
                        .groupId(new ObjectId(reportPost.getGroupId()))
                        .type(NotificationType.REPORT_REJECTED)
                        .build()
                );
            }
        }
        
        return reportPostRepository.save(reportPost);
    }

    @Override
    public Map<String, Object> analyzeReportContent(String reportId) {
        ReportPost reportPost = reportPostRepository.findById(new ObjectId(reportId))
                .orElseThrow(() -> new RuntimeException("Report not found"));
        
        // Get the post content
        GroupPost post = groupPostRepository.findById(new ObjectId(reportPost.getPostId()))
                .orElseThrow(() -> new RuntimeException("Post not found"));

        String url = "https://api.openai.com/v1/moderations";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer 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");

        // Combine post content for analysis
        String contentToAnalyze = String.format("Title: %s\nContent: %s", 
            post.getTitle(), 
            post.getContent()
        );

        Map<String, Object> requestBody = Map.of(
            "model", "omni-moderation-latest",
            "input", contentToAnalyze
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        Map<String, Object> moderationResult = restTemplate.postForObject(url, request, Map.class);

        // Add post information to the result
        Map<String, Object> result = new HashMap<>();
        result.put("moderation", moderationResult);
        result.put("post", Map.of(
            "title", post.getTitle(),
            "content", post.getContent()
        ));

        return result;
    }
} 