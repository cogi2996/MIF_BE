package com.mif.movieInsideForum.Module.ActivityAnalytics.repository;

import com.mif.movieInsideForum.Module.ActivityAnalytics.entity.UserActivityScore;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserActivityScoreRepository extends MongoRepository<UserActivityScore, String> {
    Optional<UserActivityScore> findByUserIdAndGroupId(String userId, String groupId);
} 