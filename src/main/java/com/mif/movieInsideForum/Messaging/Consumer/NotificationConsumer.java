package com.mif.movieInsideForum.Messaging.Consumer;

import com.mif.movieInsideForum.Collection.Notification.Notification;
import com.mif.movieInsideForum.Queue.NotificationQueueDefine;
import com.mif.movieInsideForum.Module.Notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {
    private final NotificationService notificationService; // Dịch vụ xử lý thông báo

    @RabbitListener(queues = NotificationQueueDefine.NOTIFICATION_QUEUE)
    public void receiveNotification(Notification notification) {
        log.info("Received notification: {}", notification);
        // Kiểm tra loại thông báo và xử lý tương ứng
        switch (notification.getType()) {
            case JOIN_REQUEST:
                if (notification.getIsRemove()) {
                    notificationService.deleteNotificationByGroupIdAndSenderIdAndTypeJoinRequest(notification.getGroupId(), notification.getSenderId());
                    break;
                }
                if (notification.getGroupId() != null) {
                    // Xử lý thông báo yêu cầu tham gia nhóm
                    notificationService.handleJoinRequest(notification);
                }
                break;

            case UP_VOTE:
            case REMOVE_VOTE:
            case DOWN_VOTE:
                if (notification.getIsRemove()) {
                    notificationService.deleteNotificationByGroupPostIdAndSenderIdAndTypeUpVote(notification.getGroupPostId(), notification.getSenderId());
                    break;
                }
                if (notification.getGroupPostId() != null) {
                    // Xử lý thông báo bình chọn bài viết
                    notificationService.handlePostVote(notification);
                }
                break;
            case EVENT:
                // case remove
                //case handle
                log.info("Received event notification: {}", notification);
                if(notification.getGroupId()!=null){
                    notificationService.handleEventNotification(notification);
                }
                break;

            case BADGE_EARNED:
                log.info("Received badge notification: {}", notification);
                notificationService.handleBadgeNotification(notification);
                break;

            case POST_BLOCKED:
                log.info("Received post blocked notification: {}", notification);
                if (notification.getGroupPostId() != null ) {
                    notificationService.handlePostBlockedNotification(notification);
                }
                break;

            case REPORT_REJECTED:
                log.info("Received report rejected notification: {}", notification);
                if (notification.getGroupPostId() != null ) {
                    notificationService.handleReportRejectedNotification(notification);
                }
                break;

            default:
                // Xử lý loại thông báo không xác định
                log.warn("Received unsupported notification type: {}", notification.getType());
                break;
        }
    }


}