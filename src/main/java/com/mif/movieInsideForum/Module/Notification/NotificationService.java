package com.mif.movieInsideForum.Module.Notification;

import com.mif.movieInsideForum.Collection.Notification.Notification;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface NotificationService {
    void handleJoinRequest(Notification notification);
    void handlePostVote(Notification notification);
    void deleteNotificationByGroupPostIdAndSenderIdAndTypeUpVote(ObjectId groupPostId, ObjectId senderId);
    void deleteNotificationByGroupIdAndSenderIdAndTypeJoinRequest(ObjectId groupId, ObjectId senderId);
    Slice<Notification> getAllWithPaging(Pageable pageable, ObjectId receiverId);
    Notification markAsRead(ObjectId notificationId);
    void handleEventNotification(Notification notification);
    long countUnreadNotifications(ObjectId receiverId);
    void handleBadgeNotification(Notification notification);
    void handlePostBlockedNotification(Notification notification);
    void handleReportRejectedNotification(Notification notification);
}