package com.mif.movieInsideForum.Module.ActivityAnalytics.consumer;

import com.mif.movieInsideForum.Module.ActivityAnalytics.dto.BadgeEvaluationDTO;
import com.mif.movieInsideForum.Module.ActivityAnalytics.entity.UserActivityScore;
import com.mif.movieInsideForum.Module.ActivityAnalytics.repository.UserActivityScoreRepository;
import com.mif.movieInsideForum.Module.ActivityAnalytics.service.BadgeService;
import com.mif.movieInsideForum.Queue.BadgeQueueDefine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BadgeEvaluationConsumer {
    private final UserActivityScoreRepository scoreRepository;
    private final BadgeService badgeService;

    @RabbitListener(queues = BadgeQueueDefine.BADGE_EVALUATION_QUEUE)
    public void processBadgeEvaluation(BadgeEvaluationDTO dto) {
        log.info("Processing badge evaluation for userId={}, groupId={}", 
            dto.getUserId(), dto.getGroupId());
        
        try {
            // Lấy điểm hoạt động của user trong nhóm
            UserActivityScore score = scoreRepository.findByUserIdAndGroupId(dto.getUserId(), dto.getGroupId())
                .orElseThrow(() -> new RuntimeException("User activity score not found"));

            // Đánh giá và trao huy hiệu
            badgeService.evaluateAndAssignBadges(score);
            
        } catch (Exception e) {
            log.error("Error processing badge evaluation for userId={}, groupId={}, error={}", 
                dto.getUserId(), dto.getGroupId(), e.getMessage(), e);
            throw e;
        }
    }
}