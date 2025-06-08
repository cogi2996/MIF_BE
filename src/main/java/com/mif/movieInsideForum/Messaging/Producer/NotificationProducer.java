package com.mif.movieInsideForum.Messaging.Producer;

import com.mif.movieInsideForum.Queue.NotificationQueueDefine;
import com.mif.movieInsideForum.Collection.Notification.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationProducer {
    private final RabbitTemplate rabbitTemplate;

    public void sendNotification(Notification notification) {
        log.info("Sending notification: " + notification);
        rabbitTemplate.convertAndSend(
                NotificationQueueDefine.NOTIFICATION_EXCHANGE_NAME,
                NotificationQueueDefine.NOTIFICATION_ROUTING_KEY,
                notification);
    }
}