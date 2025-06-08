package com.mif.movieInsideForum.Queue;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class EmailDelayQueueDefine {

    public static final String EMAIL_DELAY_QUEUE = "emailDelayQueue";
    public static final String EMAIL_DLX_EXCHANGE = "emailDelayExchange";
    public static final String EMAIL_DLX_ROUTING_KEY = "emailDelayRoutingKey";
    public static final String EMAIL_DLX_QUEUE = "emailDLXQueue";

    @Bean
    public Queue emailDelayQueue() {
        return QueueBuilder.durable(EMAIL_DELAY_QUEUE)
                .withArgument("x-dead-letter-exchange", EMAIL_DLX_EXCHANGE) // Ensure this exchange exists
                .withArgument("x-dead-letter-routing-key", EMAIL_DLX_ROUTING_KEY) // Routing key
                .build();
    }

    @Bean
    public DirectExchange emailDelayExchange() {
        return new DirectExchange(EMAIL_DLX_EXCHANGE);
    }

    @Bean
    public Queue emailDLXQueue() {
        return QueueBuilder.durable(EMAIL_DLX_QUEUE).build(); // Ensure this queue exists
    }

    @Bean
    public Binding emailDLXBinding(DirectExchange emailDelayExchange, Queue emailDLXQueue) {
        return BindingBuilder.bind(emailDLXQueue).to(emailDelayExchange).with(EMAIL_DLX_ROUTING_KEY);
    }

    @Bean
    public Binding emailDelayQueueBinding(DirectExchange emailDelayExchange, Queue emailDelayQueue) {
        return BindingBuilder.bind(emailDelayQueue).to(emailDelayExchange).with(EMAIL_DLX_ROUTING_KEY);
    }
}

