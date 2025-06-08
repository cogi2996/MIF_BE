package com.mif.movieInsideForum.Messaging.Consumer;


import com.mif.movieInsideForum.DTO.EmailMessage;
import com.mif.movieInsideForum.Queue.EmailQueueDefine;
import com.mif.movieInsideForum.Util.MyEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailConsumer {

    private final MyEmailService emailService;

    @RabbitListener(queues = EmailQueueDefine.EMAIL_QUEUE)
    public void receiveEmail(EmailMessage emailMessage) {
        emailService.sendEmail(emailMessage.getEmail(), emailMessage.getSubject(), emailMessage.getBody());
    }
}