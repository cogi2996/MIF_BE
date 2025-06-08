package com.mif.movieInsideForum.Messaging.Producer;

import com.mif.movieInsideForum.DTO.message.EventMessage;
import com.mif.movieInsideForum.Queue.EmailDelayQueueDefine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailDelayProducer {

    private final RabbitTemplate rabbitTemplate;

    // Gửi message với delay vào RabbitMQ
    public void sendDelayedMessage(EventMessage eventMessage) {
        rabbitTemplate.convertAndSend(EmailDelayQueueDefine.EMAIL_DLX_EXCHANGE, EmailDelayQueueDefine.EMAIL_DLX_ROUTING_KEY, eventMessage);
    }

}
