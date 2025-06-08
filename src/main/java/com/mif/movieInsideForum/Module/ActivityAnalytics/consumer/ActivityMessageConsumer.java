package com.mif.movieInsideForum.Module.ActivityAnalytics.consumer;

import com.mif.movieInsideForum.Module.ActivityAnalytics.activity.ActivityMessage;
import com.mif.movieInsideForum.Module.ActivityAnalytics.dto.BadgeEvaluationDTO;
import com.mif.movieInsideForum.Module.ActivityAnalytics.service.ActivityScoreService;
import com.mif.movieInsideForum.Queue.BadgeQueueDefine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActivityMessageConsumer {
    private final ActivityScoreService activityScoreService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "activity.tracking")
    public void processActivityMessage(ActivityMessage message) {
        log.info("Processing activity message: type={}, userId={}, groupId={}", 
            message.getActivityType(), message.getUserId(), message.getGroupId());
            
        try {
            String userId = message.getUserId();
            String groupId = message.getGroupId();

            // Xử lý điểm cho người thực hiện hành động
            processActorScore(message, userId, groupId);

            // Xử lý điểm cho người nhận hành động (nếu có)
            processReceiverScore(message, groupId);

            // Gửi message để đánh giá huy hiệu
            BadgeEvaluationDTO dto = new BadgeEvaluationDTO(userId, groupId);
            rabbitTemplate.convertAndSend(
                BadgeQueueDefine.BADGE_EXCHANGE,
                BadgeQueueDefine.BADGE_EVALUATION_ROUTING_KEY,
                dto
            );

        } catch (Exception e) {
            log.error("Error processing activity message: type={}, userId={}, groupId={}, error={}", 
                message.getActivityType(), message.getUserId(), message.getGroupId(), e.getMessage(), e);
            throw e;
        }
    }

    private void processActorScore(ActivityMessage message, String userId, String groupId) {
        log.debug("Processing actor score for userId={}, groupId={}", userId, groupId);
        
        switch (message.getActivityType()) {
            case GROUP_JOINED:
                // Tham gia nhóm: 20 điểm
                activityScoreService.updateGroupJoinScore(userId, groupId);
                break;

            case GROUP_POST_CREATED:
                // Đăng bài viết: 10 điểm
                activityScoreService.updatePostScore(userId, groupId);
                break;

            case GROUP_POST_LIKED:
                // Like bài viết: 1 điểm
                activityScoreService.updateLikeScore(userId, groupId);
                break;

            case GROUP_COMMENT_CREATED:
                // Bình luận: 5 điểm
                activityScoreService.updateCommentScore(userId, groupId);
                break;

            case GROUP_COMMENT_LIKED:
                // Like bình luận: 1 điểm
                activityScoreService.updateLikeScore(userId, groupId);
                break;

            case GROUP_EVENT_JOINED:
                // Tham gia sự kiện: 15 điểm
                activityScoreService.updateEventJoinScore(userId, groupId);
                break;



            default:
                log.warn("Unknown activity type for actor score: {}", message.getActivityType());
        }
    }

    private void processReceiverScore(ActivityMessage message, String groupId) {
        // Chỉ xử lý điểm cho người nhận nếu có thông tin người nhận
        if (message.getPostOwnerId() != null) {
            log.debug("Processing receiver score for postOwnerId={}, groupId={}", 
                message.getPostOwnerId(), groupId);
                
            switch (message.getActivityType()) {
                case GROUP_POST_LIKED:
                    // Nhận like bài viết: 2 điểm
                    activityScoreService.updateReceivedLikeScore(message.getPostOwnerId(), groupId);
                    break;

                case GROUP_COMMENT_CREATED:
                    // Nhận bình luận: 3 điểm
                    activityScoreService.updateReceivedCommentScore(message.getPostOwnerId(), groupId);
                    break;

                case GROUP_COMMENT_LIKED:
                    // Nhận like bình luận: 2 điểm
                    activityScoreService.updateReceivedLikeScore(message.getCommentOwnerId(), groupId);
                    break;

                default:
                    // Các trường hợp khác không cần xử lý điểm cho người nhận
                    break;
            }
        }
    }
} 