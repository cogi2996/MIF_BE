package com.mif.movieInsideForum.Module.ActivityAnalytics.service;

import com.mif.movieInsideForum.Module.ActivityAnalytics.entity.UserActivityScore;
import com.mif.movieInsideForum.Module.ActivityAnalytics.enums.BadgeLevel;
import com.mif.movieInsideForum.Module.ActivityAnalytics.repository.UserActivityScoreRepository;
import com.mif.movieInsideForum.Collection.User;
import com.mif.movieInsideForum.Module.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityScoreService {
    private final UserActivityScoreRepository scoreRepository;
    private final UserService userService;
    private final MongoTemplate mongoTemplate;

    // Score constants
    private static final int POST_SCORE = 10;
    private static final int COMMENT_SCORE = 5;
    private static final int LIKE_SCORE = 1;
    private static final int RECEIVED_LIKE_SCORE = 2;
    private static final int RECEIVED_COMMENT_SCORE = 3;
    private static final int GROUP_JOIN_SCORE = 20;
    private static final int EVENT_JOIN_SCORE = 15;

    @Transactional
    public void updateGroupJoinScore(String userId, String groupId) {
        UserActivityScore score = getOrCreateScore(userId, groupId);
        score.setGroupJoinScore(score.getGroupJoinScore() + GROUP_JOIN_SCORE);
        updateTotalScore(score);
        scoreRepository.save(score);
    }

    @Transactional
    public void updatePostScore(String userId, String groupId) {
        UserActivityScore score = getOrCreateScore(userId, groupId);
        score.setPostScore(score.getPostScore() + POST_SCORE);
        updateTotalScore(score);
        scoreRepository.save(score);
    }

    @Transactional
    public void updateCommentScore(String userId, String groupId) {
        UserActivityScore score = getOrCreateScore(userId, groupId);
        score.setCommentScore(score.getCommentScore() + COMMENT_SCORE);
        updateTotalScore(score);
        scoreRepository.save(score);
    }

    @Transactional
    public void updateLikeScore(String userId, String groupId) {
        UserActivityScore score = getOrCreateScore(userId, groupId);
        score.setLikeScore(score.getLikeScore() + LIKE_SCORE);
        updateTotalScore(score);
        scoreRepository.save(score);
    }

    @Transactional
    public void updateReceivedLikeScore(String userId, String groupId) {
        UserActivityScore score = getOrCreateScore(userId, groupId);
        score.setReceivedLikeScore(score.getReceivedLikeScore() + RECEIVED_LIKE_SCORE);
        updateTotalScore(score);
        scoreRepository.save(score);
    }

    @Transactional
    public void updateReceivedCommentScore(String userId, String groupId) {
        UserActivityScore score = getOrCreateScore(userId, groupId);
        score.setReceivedCommentScore(score.getReceivedCommentScore() + RECEIVED_COMMENT_SCORE);
        updateTotalScore(score);
        scoreRepository.save(score);
    }

    @Transactional
    public void updateEventJoinScore(String userId, String groupId) {
        UserActivityScore score = getOrCreateScore(userId, groupId);
        score.setEventScore(score.getEventScore() + EVENT_JOIN_SCORE);
        updateTotalScore(score);
        scoreRepository.save(score);
    }

    private UserActivityScore getOrCreateScore(String userId, String groupId) {
        return scoreRepository.findByUserIdAndGroupId(userId, groupId)
            .orElseGet(() -> createNewScore(userId, groupId));
    }

    private UserActivityScore createNewScore(String userId, String groupId) {
        return UserActivityScore.builder()
            .userId(userId)
            .groupId(groupId)
            .totalScore(0)
            .groupJoinScore(0)
            .postScore(0)
            .commentScore(0)
            .likeScore(0)
            .receivedLikeScore(0)
            .receivedCommentScore(0)
            .eventScore(0)
            .badgeLevel(null)
            .createdAt(LocalDateTime.now())
            .lastUpdated(LocalDateTime.now())
            .build();
    }

    private void updateTotalScore(UserActivityScore score) {
        score.setTotalScore(
            score.getGroupJoinScore() +
            score.getPostScore() +
            score.getCommentScore() +
            score.getLikeScore() +
            score.getReceivedLikeScore() +
            score.getReceivedCommentScore() +
            score.getEventScore()
        );
        score.setLastUpdated(LocalDateTime.now());
    }

    public List<Map<String, Object>> getTopActiveUsersInGroup(String groupId, int limit) {
        // 1. Lấy top N UserActivityScore theo groupId, sort totalScore giảm dần
        Query query = new Query();
        query.addCriteria(Criteria.where("groupId").is(groupId));
        query.with(Sort.by(Sort.Direction.DESC, "totalScore"));
        query.limit(limit);
        List<UserActivityScore> scores = mongoTemplate.find(query, UserActivityScore.class);

        // 2. Lấy danh sách userId
        List<org.bson.types.ObjectId> userIds = scores.stream()
            .map(s -> new org.bson.types.ObjectId(s.getUserId()))
            .collect(Collectors.toList());

        // 3. Lấy thông tin user
        List<User> users = userService.findUsersByIds(userIds);
        Map<String, User> userMap = users.stream()
            .collect(Collectors.toMap(u -> u.getId().toString(), u -> u));

        // 4. Map kết quả trả về: user info + totalScore
        return scores.stream().map(score -> {
            User user = userMap.get(score.getUserId());
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("userId", score.getUserId());
            map.put("displayName", user != null ? user.getDisplayName() : null);
            map.put("totalScore", score.getTotalScore());
            map.put("avatar", user != null ? user.getProfilePictureUrl() : null);
            map.put("badgeLevel", score.getBadgeLevel());      
            
            return map;
        }).collect(java.util.stream.Collectors.toList());
    }
} 