package com.mif.movieInsideForum.Module.Notification;

import com.mif.movieInsideForum.Module.Post.entity.GroupPost;
import com.mif.movieInsideForum.Collection.Notification.Notification;
import com.mif.movieInsideForum.Module.Post.repository.GroupPostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final SimpMessagingTemplate messagingTemplate;
    private final GroupPostRepository groupPostRepository;
    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public void handleJoinRequest(Notification notification) {
        log.info("ID người nhận: " + notification.getReceiverIdAsString());

        // Lưu thông báo vào cơ sở dữ liệu
        Notification dbNotification = notificationRepository.save(notification);

        // Gửi thông báo qua WebSocket
        messagingTemplate.convertAndSendToUser(
                notification.getReceiverIdAsString(),
                "/queue/notifications",
                dbNotification
        );

        log.info("Xử lý yêu cầu tham gia nhóm cho nhóm ID: " + notification.getGroupId());
    }

    @Override
    @Transactional
    public void handlePostVote(Notification notification) {
        log.info("group_post_id_2: " +notification.getGroupPostId());
        // Kiểm tra xem thông báo đã tồn tại chưa
        Optional<Notification> existingNotificationOpt = notificationRepository.findByGroupPostIdAndReceiverId(
                notification.getGroupPostId(),
                notification.getReceiverId()
        );
        if (existingNotificationOpt.isPresent()) {
            log.info("Thông báo đã tồn tại: " + notification.getGroupPostId());
            return;
        }

        // Lấy thông tin bài viết
        Optional<GroupPost> postOpt = groupPostRepository.findById(notification.getGroupPostId());
        if (postOpt.isEmpty()) {
            log.error("Bài viết không tồn tại: " + notification.getGroupPostId());
            return;
        }

        GroupPost dbPost = postOpt.get();
        ObjectId adminId = dbPost.getOwner().getId();
        String groupName = dbPost.getGroup().getGroupName();

        // Tạo thông báo mới
        Notification updateNotification = Notification.builder()
                .groupPostId(notification.getGroupPostId())
                .groupName(groupName)
                .message(notification.getMessage())
                .senderId(notification.getSenderId())
                .receiverId(adminId)
                .groupId(notification.getGroupId())
                .type(notification.getType())
                .build();

        // Lưu vào cơ sở dữ liệu
        Notification dbNotification = notificationRepository.save(updateNotification);

        // Gửi thông báo qua WebSocket
        messagingTemplate.convertAndSendToUser(
                updateNotification.getReceiverIdAsString(),
                "/queue/notifications",
                dbNotification
        );

        log.info("Xử lý thông báo bình chọn bài viết cho bài viết ID: " + dbNotification.getGroupPostId());
    }
    @Override
    @Transactional
    public void deleteNotificationByGroupPostIdAndSenderIdAndTypeUpVote(ObjectId groupPostId, ObjectId senderId) {
        log.info("removed notify reaction post: " + groupPostId);
        notificationRepository.deleteByGroupPostIdAndSenderIdAndTypeUpVoteOrDownVote(groupPostId, senderId);
    }

    @Override
    @Transactional
    public void deleteNotificationByGroupIdAndSenderIdAndTypeJoinRequest(ObjectId groupId, ObjectId senderId) {
        log.info("removed notify join request groupId: " + groupId);
        log.info("removed notify join request senderId: " + senderId);
        notificationRepository.deleteByGroupIdAndSenderIdAndTypeJoinRequest(groupId, senderId);
    }


    @Override
    public Slice<Notification> getAllWithPaging(Pageable pageable, ObjectId receiverId) {
        return notificationRepository.findAllWithPag(pageable, receiverId);
    }

    @Override
    @Transactional
    public Notification markAsRead(ObjectId notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.setIsRead(true);
            return notificationRepository.save(notification);
        }
        return null;
    }

    @Override
    @Transactional
    public void handleEventNotification(Notification notification) {
        log.info("ID group: " + notification.getGroupId());
        // Lưu thông báo vào cơ sở dữ liệu
        Notification dbNotification = notificationRepository.save(notification);
        //log event Id
        log.info("event_id: " + notification.getEventId());
        // Gửi thông báo qua WebSocket
        messagingTemplate.convertAndSendToUser(
                notification.getReceiverIdAsString(),
                "/queue/notifications",
                dbNotification
        );

        log.info("Xử lý thông báo sự kiện cho sự kiện ID: " + notification.getEventId());

    }

    @Override
    public long countUnreadNotifications(ObjectId receiverId) {
        return notificationRepository.countUnreadNotifications(receiverId);
    }

    @Override
    @Transactional
    public void handleBadgeNotification(Notification notification) {
        log.info("Handling badge notification for user: {}", notification.getReceiverId());
        
        // Lưu thông báo vào cơ sở dữ liệu
        Notification dbNotification = notificationRepository.save(notification);
        
        // Gửi thông báo qua WebSocket
        messagingTemplate.convertAndSendToUser(
            notification.getReceiverIdAsString(),
            "/queue/notifications",
            dbNotification
        );
        
        log.info("Successfully handled badge notification for user: {}", notification.getReceiverId());
    }

    @Override
    @Transactional
    public void handlePostBlockedNotification(Notification notification) {
        log.info("Handling post blocked notification for user: {}", notification.getReceiverId());
        
        // Lưu thông báo vào cơ sở dữ liệu
        Notification dbNotification = notificationRepository.save(notification);
        
        // Gửi thông báo qua WebSocket
        messagingTemplate.convertAndSendToUser(
            notification.getReceiverIdAsString(),
            "/queue/notifications",
            dbNotification
        );
        
        log.info("Successfully handled post blocked notification for user: {}", notification.getReceiverId());
    }

    @Override
    public void handleReportRejectedNotification(Notification notification) {
        log.info("Handling report rejected notification for user: {}", notification.getReceiverIdAsString());

        // Lưu thông báo vào database
        Notification dbNotification = notificationRepository.save(notification);
        
        // Gửi thông báo qua WebSocket
        messagingTemplate.convertAndSendToUser(
            notification.getReceiverIdAsString(),
            "/queue/notifications",
            dbNotification
        );
        
        log.info("Successfully handled report rejected notification for user: {}", notification.getReceiverIdAsString());
    }

}