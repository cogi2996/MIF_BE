package com.mif.movieInsideForum.Messaging.Consumer;

import com.mif.movieInsideForum.DTO.message.EventMessage;
import com.mif.movieInsideForum.Queue.EmailDelayQueueDefine;
import com.mif.movieInsideForum.Util.MyEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;  // Import @Slf4j
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j  // Sử dụng @Slf4j để tạo logger tự động
public class EmailDelayConsumer {
    private final MyEmailService emailService;

    @RabbitListener(queues = EmailDelayQueueDefine.EMAIL_DELAY_QUEUE)
    public void receiveEventNotification(EventMessage eventMessage) {
        log.info("Đã nhận thông báo 2: {}", eventMessage);
        // Gửi email qua service
        emailService.sendEmail(eventMessage.getSubscriberEmail(), eventMessage.getSubject(), eventMessage.getContent());
    }
}
