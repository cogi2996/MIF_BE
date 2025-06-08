package com.mif.movieInsideForum.Job;

import com.mif.movieInsideForum.Collection.Notification.Notification;
import com.mif.movieInsideForum.DTO.message.EventMessage;
import com.mif.movieInsideForum.Messaging.Producer.EmailDelayProducer;
import com.mif.movieInsideForum.Messaging.Producer.NotificationProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class EmailNotificationJob implements Job {

    private final EmailDelayProducer emailDelayProducer;
    private final NotificationProducer notificationProducer;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();

        EventMessage eventMessage = (EventMessage) jobDataMap.get("eventMessage");
        Notification notification = (Notification) jobDataMap.get("notification");

        log.info("Executing job for Event ID: {}", eventMessage.getEventId());

        // Gửi message qua RabbitMQ Producer
        emailDelayProducer.sendDelayedMessage(eventMessage);
        // Gửi thông báo qua RabbitMQ Producer
        notificationProducer.sendNotification(notification);
    }
}