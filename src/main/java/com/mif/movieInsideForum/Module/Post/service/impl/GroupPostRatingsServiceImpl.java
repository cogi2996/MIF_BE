package com.mif.movieInsideForum.Module.Post.service.impl;

import com.mif.movieInsideForum.Module.Post.repository.GroupPostRatingsRepository;
import com.mif.movieInsideForum.Module.Post.service.GroupPostRatingsService;
import com.mif.movieInsideForum.Module.Post.entity.GroupPost;
import com.mif.movieInsideForum.Collection.Notification.NotificationType;
import com.mif.movieInsideForum.Collection.Field.VoteType;
import com.mif.movieInsideForum.Collection.GroupPostRatings;
import com.mif.movieInsideForum.Collection.Notification.Notification;
import com.mif.movieInsideForum.Messaging.Producer.NotificationProducer;
import com.mif.movieInsideForum.Module.ActivityAnalytics.activity.ActivityMessageService;
import com.mif.movieInsideForum.Module.Post.repository.GroupPostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupPostRatingsServiceImpl implements GroupPostRatingsService {
    private final GroupPostRatingsRepository groupPostRatingsRepository;
    private final NotificationProducer notificationProducer;
    private final GroupPostRepository groupPostRepository;
    private final ActivityMessageService activityMessageService;

    @Override
    @Transactional
    public void upVote(ObjectId postId, ObjectId userId) {
        if (postId == null || userId == null) {
            throw new IllegalArgumentException("Post ID and User ID must not be null");
        }

        Optional<GroupPostRatings> ratingOpt = groupPostRatingsRepository.findByPostIdAndUserId(postId, userId);
        Optional<GroupPost> groupPostOpt = groupPostRepository.findById(postId);

        if (groupPostOpt.isEmpty()) {
            throw new RuntimeException("GroupPost not found");
        }
        GroupPost groupPost = groupPostOpt.get();

        if (ratingOpt.isPresent()) {
            GroupPostRatings rating = ratingOpt.get();
            if (rating.getRatings() == VoteType.DOWNVOTE) {
                groupPost.setRatingCount(groupPost.getRatingCount() + 2);
                // Send activity message for upvote
                activityMessageService.sendGroupPostLiked(
                    userId.toString(),
                    postId.toString(),
                    groupPost.getGroup().getId().toString(),
                    groupPost.getOwner().getId().toString()
                );
            }
            rating.setRatings(VoteType.UPVOTE);
            groupPostRatingsRepository.save(rating);
        } else {
            GroupPostRatings rating = GroupPostRatings.builder()
                    .postId(postId)
                    .userId(userId)
                    .groupId(groupPost.getGroup().getId())
                    .ratings(VoteType.UPVOTE)
                    .build();
            groupPostRatingsRepository.save(rating);
            
            // Send activity message for upvote
            activityMessageService.sendGroupPostLiked(
                userId.toString(),
                postId.toString(),
                groupPost.getGroup().getId().toString(),
                groupPost.getOwner().getId().toString()
            );

            if( !userId.equals(groupPost.getOwner().getId())  ) {
                notificationProducer.sendNotification(
                        Notification.builder()
                                .groupPostId(postId)
                                .message("Your post has received an upvote!")
                                .senderId(userId)
                                .groupId(rating.getGroupId())
                                .type(NotificationType.UP_VOTE)
                                .build()
                );
            }

            groupPost.setRatingCount(groupPost.getRatingCount() + 1);
        }
        groupPostRepository.save(groupPost);
    }

    @Override
    @Transactional
    public void downVote(ObjectId postId, ObjectId userId) {
        Optional<GroupPostRatings> ratingOpt = groupPostRatingsRepository.findByPostIdAndUserId(postId, userId);
        Optional<GroupPost> groupPostOpt = groupPostRepository.findById(postId);

        if (groupPostOpt.isEmpty()) {
            throw new RuntimeException("GroupPost not found");
        }
        GroupPost groupPost = groupPostOpt.get();

        if (ratingOpt.isPresent()) {
            GroupPostRatings rating = ratingOpt.get();
            if (rating.getRatings() == VoteType.UPVOTE) {
                groupPost.setRatingCount(groupPost.getRatingCount() - 2);
                // Remove previous upvote activity
                activityMessageService.sendGroupPostLiked(
                    userId.toString(),
                    postId.toString(),
                    groupPost.getGroup().getId().toString(),
                    groupPost.getOwner().getId().toString()
                );
            }
            rating.setRatings(VoteType.DOWNVOTE);
            groupPostRatingsRepository.save(rating);
        } else {
            GroupPostRatings rating = GroupPostRatings.builder()
                    .postId(postId)
                    .userId(userId)
                    .groupId(groupPost.getGroup().getId())
                    .ratings(VoteType.DOWNVOTE)
                    .build();
            groupPostRatingsRepository.save(rating);

            if( !userId.equals(groupPost.getOwner().getId())  ) {
                notificationProducer.sendNotification(
                        Notification.builder()
                                .groupPostId(postId)
                                .message("Your post has received a downvote.")
                                .senderId(userId)
                                .groupId(rating.getGroupId())
                                .type(NotificationType.DOWN_VOTE)
                                .build()
                );
            }
            groupPost.setRatingCount(groupPost.getRatingCount() - 1);
        }
        groupPostRepository.save(groupPost);
    }

    @Override
    @Transactional
    public void removeVote(ObjectId postId, ObjectId userId) {
        Optional<GroupPost> groupPostOpt = groupPostRepository.findById(postId);

        if (groupPostOpt.isEmpty()) {
            throw new RuntimeException("GroupPost not found");
        }
        GroupPost groupPost = groupPostOpt.get();

        Optional<GroupPostRatings> ratingOpt = groupPostRatingsRepository.findByPostIdAndUserId(postId, userId);
        if (ratingOpt.isPresent()) {
            GroupPostRatings rating = ratingOpt.get();
            if (rating.getRatings() == VoteType.UPVOTE) {
                groupPost.setRatingCount(groupPost.getRatingCount() - 1);
                // Remove upvote activity
                activityMessageService.sendGroupPostLiked(
                    userId.toString(),
                    postId.toString(),
                    groupPost.getGroup().getId().toString(),
                    groupPost.getOwner().getId().toString()
                );
            } else if (rating.getRatings() == VoteType.DOWNVOTE) {
                groupPost.setRatingCount(groupPost.getRatingCount() + 1);
            }
            groupPostRatingsRepository.delete(rating);
            groupPostRepository.save(groupPost);
            notificationProducer.sendNotification(
                    Notification.builder()
                            .groupPostId(postId)
                            .senderId(userId)
                            .isRemove(true)
                            .type(NotificationType.REMOVE_VOTE)
                            .build()
            );
        } else {
            throw new RuntimeException("Vote not found");
        }
    }

    @Override
    public int getCurrentVotes(ObjectId postId) {
        return groupPostRatingsRepository.getTotalRating(postId).orElse(0);
    }

    @Override
    public VoteType getUserVote(ObjectId postId, ObjectId userId) {
        Optional<GroupPostRatings> ratingOpt = groupPostRatingsRepository.findByPostIdAndUserId(postId, userId);
        return ratingOpt.map(GroupPostRatings::getRatings).orElse(null);
    }
}