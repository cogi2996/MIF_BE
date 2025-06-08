package com.mif.movieInsideForum.Module.ActivityAnalytics.service;

import com.mif.movieInsideForum.Module.ActivityAnalytics.dto.BadgeInfoDTO;
import com.mif.movieInsideForum.Module.ActivityAnalytics.entity.UserActivityScore;
import com.mif.movieInsideForum.Module.ActivityAnalytics.enums.BadgeLevel;
import com.mif.movieInsideForum.Module.ActivityAnalytics.repository.UserActivityScoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BadgeInfoService {
    private final UserActivityScoreRepository scoreRepository;

    public BadgeInfoDTO getBadgeInfo(String userId, String groupId) {
        log.info("Getting badge info for userId={}, groupId={}", userId, groupId);
        
        Optional<UserActivityScore> scoreOpt = scoreRepository.findByUserIdAndGroupId(userId, groupId);
        
        if (scoreOpt.isEmpty()) {
            return BadgeInfoDTO.builder()
                    .userId(userId)
                    .groupId(groupId)
                    .badgeLevel(null)
                    .totalScore(0)
                    .badgeName("Chưa có huy hiệu")
                    .badgeDescription("Bạn chưa có huy hiệu nào trong nhóm này")
                    .build();
        }

        UserActivityScore score = scoreOpt.get();
        BadgeLevel badgeLevel = score.getBadgeLevel();
        
        return BadgeInfoDTO.builder()
                .userId(userId)
                .groupId(groupId)
                .badgeLevel(badgeLevel)
                .totalScore(score.getTotalScore())
                .badgeName(badgeLevel != null ? badgeLevel.getLevel() : "Chưa có huy hiệu")
                .badgeDescription(getBadgeDescription(badgeLevel, score.getTotalScore()))
                .build();
    }

    private String getBadgeDescription(BadgeLevel badgeLevel, Integer totalScore) {
        if (badgeLevel == null) {
            return "Bạn cần đạt 100 điểm để nhận huy hiệu Đồng";
        }

        switch (badgeLevel) {
            case BRONZE:
                return String.format("Bạn đã đạt huy hiệu Đồng với %d điểm. Cần thêm %d điểm để đạt huy hiệu Bạc", 
                    totalScore, 500 - totalScore);
            case SILVER:
                return String.format("Bạn đã đạt huy hiệu Bạc với %d điểm. Cần thêm %d điểm để đạt huy hiệu Vàng", 
                    totalScore, 1000 - totalScore);
            case GOLD:
                return String.format("Bạn đã đạt huy hiệu Vàng với %d điểm. Cần thêm %d điểm để đạt huy hiệu Bạch Kim", 
                    totalScore, 2000 - totalScore);
            case PLATINUM:
                return String.format("Chúc mừng! Bạn đã đạt huy hiệu Bạch Kim với %d điểm", totalScore);
            default:
                return "Chưa có huy hiệu";
        }
    }
} 