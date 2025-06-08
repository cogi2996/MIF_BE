package com.mif.movieInsideForum.Module.Comment;

import com.mif.movieInsideForum.Collection.Comment;
import com.mif.movieInsideForum.Collection.Group;
import com.mif.movieInsideForum.Module.Post.entity.GroupPost;
import com.mif.movieInsideForum.Collection.User;
import com.mif.movieInsideForum.Module.ActivityAnalytics.dto.SimpleBadgeDTO;
import com.mif.movieInsideForum.Module.ActivityAnalytics.enums.BadgeLevel;
import com.mif.movieInsideForum.Module.Post.repository.GroupPostRepository;
import com.mif.movieInsideForum.Module.ActivityAnalytics.activity.ActivityMessageService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final GroupPostRepository groupPostRepository;
    private final ActivityMessageService activityMessageService;

    @Override
    public Comment createComment(Comment comment, User user) {
        // Validate postId and get group
        GroupPost groupPost = groupPostRepository.findById(comment.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid postId"));
        Group group = groupPost.getGroup();

        // Set group and user details
        comment.setGroupId(group.getId());
        comment.setUsername(user.getDisplayName());
        comment.setUserId(user.getId());
        comment.setUserAvatar(user.getProfilePictureUrl());

        // Lấy thông tin huy hiệu từ badgeMap
        BadgeLevel userBadge = user.getBadgeMap().get(group.getId().toString());
        SimpleBadgeDTO badgeInfo = userBadge != null 
            ? SimpleBadgeDTO.builder()
                .level(userBadge)
                .build()
            : SimpleBadgeDTO.builder()
                .level(null)
                .build();
        
        comment.setBadge(badgeInfo);

        // Save the comment
        Comment savedComment = commentRepository.save(comment);

        // Send activity message for comment creation
        activityMessageService.sendGroupCommentCreated(
            user.getId().toString(),
            savedComment.getId().toString(),
            groupPost.getId().toString(),
            group.getId().toString(),
            groupPost.getOwner().getId().toString(),
            user.getId().toString()
        );

        return savedComment;
    }

    @Override
    public Slice<Comment> getCommentsByPostId(ObjectId postId, Pageable pageable) {
        Slice<Comment> comments = commentRepository.getCommentsByPostId(postId, pageable);
        List<Comment> reversedComments = new ArrayList<>(comments.getContent());
        Collections.reverse(reversedComments);
        return new SliceImpl<>(reversedComments, pageable, comments.hasNext());
    }

    @Override
@Transactional
public Comment upvote(ObjectId commentId, ObjectId userId) {
    Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid commentId"));
            
    if (comment.getUpvotes().contains(userId)) {
        // Nếu đã upvote rồi thì remove upvote
        comment.getUpvotes().remove(userId);
    } else {
        // Remove downvote nếu có
        comment.getDownvotes().remove(userId);
        // Thêm upvote
        comment.getUpvotes().add(userId);
        
        // Send activity message for upvote
        activityMessageService.sendGroupCommentLiked(
            userId.toString(),
            commentId.toString(),
            comment.getPostId().toString(),
            comment.getGroupId().toString(),
            comment.getUserId().toString()
        );
    }
    
    return commentRepository.save(comment);
}

@Override
@Transactional
public Comment downvote(ObjectId commentId, ObjectId userId) {
    Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid commentId"));
            
    if (comment.getDownvotes().contains(userId)) {
        // Nếu đã downvote rồi thì remove downvote
        comment.getDownvotes().remove(userId);
    } else {
        // Remove upvote nếu có
        comment.getUpvotes().remove(userId);
        // Thêm downvote
        comment.getDownvotes().add(userId);
        
        // Send activity message for downvote
        activityMessageService.sendGroupCommentLiked(
            userId.toString(),
            commentId.toString(),
            comment.getPostId().toString(),
            comment.getGroupId().toString(),
            comment.getUserId().toString()
        );
    }
    
    return commentRepository.save(comment);
}

}
