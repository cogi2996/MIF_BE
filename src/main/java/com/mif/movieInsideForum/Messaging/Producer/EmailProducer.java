package com.mif.movieInsideForum.Messaging.Producer;


import com.mif.movieInsideForum.Queue.EmailQueueDefine;
import com.mif.movieInsideForum.DTO.EmailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailProducer {
    private final RabbitTemplate rabbitTemplate;

    public void sendEmail(String email, String subject, String body) {
        EmailMessage emailMessage = new EmailMessage(email, subject, body);
        rabbitTemplate.convertAndSend(EmailQueueDefine.EXCHANGE_NAME, EmailQueueDefine.ROUTING_KEY, emailMessage);
    }




}