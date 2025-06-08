package com.mif.movieInsideForum.Module.ActivityAnalytics.service;

import com.mif.movieInsideForum.Collection.Notification.Notification;
import com.mif.movieInsideForum.Collection.Notification.NotificationType;
import com.mif.movieInsideForum.Collection.User;
import com.mif.movieInsideForum.Messaging.Producer.NotificationProducer;
import com.mif.movieInsideForum.Module.ActivityAnalytics.dto.UserBadgeDTO;
import com.mif.movieInsideForum.Module.ActivityAnalytics.entity.UserActivityScore;
import com.mif.movieInsideForum.Module.ActivityAnalytics.enums.BadgeLevel;
import com.mif.movieInsideForum.Module.ActivityAnalytics.repository.UserActivityScoreRepository;
import com.mif.movieInsideForum.Module.Notification.NotificationRepository;
import com.mif.movieInsideForum.Module.User.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BadgeService {
    private final UserActivityScoreRepository scoreRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationProducer notificationProducer;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    @Transactional
    public void evaluateAndAssignBadges(UserActivityScore score) {
        log.info("Evaluating badges for userId={}", score.getUserId());
        
        // Kiểm tra điều kiện cho từng loại huy hiệu
        if (isEligibleForBadge(score, BadgeLevel.BRONZE)) {
            assignBadge(score, BadgeLevel.BRONZE);
        }
        
        if (isEligibleForBadge(score, BadgeLevel.SILVER)) {
            assignBadge(score, BadgeLevel.SILVER);
        }
        
        if (isEligibleForBadge(score, BadgeLevel.GOLD)) {
            assignBadge(score, BadgeLevel.GOLD);
        }
        
        if (isEligibleForBadge(score, BadgeLevel.PLATINUM)) {
            assignBadge(score, BadgeLevel.PLATINUM);
        }
    }

    private boolean isEligibleForBadge(UserActivityScore score, BadgeLevel badgeLevel) {
        // Điều kiện cho từng loại huy hiệu
        switch (badgeLevel) {
            case BRONZE:
                return score.getTotalScore() >= 100;
            case SILVER:
                return score.getTotalScore() >= 500;
            case GOLD:
                return score.getTotalScore() >= 1000;
            case PLATINUM:
                return score.getTotalScore() >= 2000;
            default:
                return false;
        }
    }

    @Transactional
    private void assignBadge(UserActivityScore score, BadgeLevel badgeLevel) {
        // Chỉ cập nhật và thông báo nếu huy hiệu mới cao hơn huy hiệu hiện tại
        if (score.getBadgeLevel() == null || badgeLevel.ordinal() > score.getBadgeLevel().ordinal()) {
            log.info("Assigning {} badge to userId={}", badgeLevel.getLevel(), score.getUserId());
            
            // Cập nhật huy hiệu
            score.setBadgeLevel(badgeLevel);
            scoreRepository.save(score);
            
            // Cập nhật badgeMap trong User
            User user = userRepository.findById(new ObjectId(score.getUserId()))
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            user.getBadgeMap().put(score.getGroupId(), badgeLevel);
            userRepository.save(user);
            
            // Tạo thông báo
            Notification notification = Notification.builder()
                .receiverId(new ObjectId(score.getUserId()))
                .groupId(new ObjectId(score.getGroupId()))
                .type(NotificationType.BADGE_EARNED)
                .message(String.format("Chúc mừng bạn đã đạt được huy hiệu %s!", badgeLevel.getLevel()))
                .build();
            
            // Gửi thông báo qua RabbitMQ thay vì WebSocket trực tiếp
            notificationProducer.sendNotification(notification);
        }
    }
}