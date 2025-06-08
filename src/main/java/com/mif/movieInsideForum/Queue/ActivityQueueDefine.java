package com.mif.movieInsideForum.Queue;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActivityQueueDefine {
    public static final String EXCHANGE_NAME = "activity.exchange";
    public static final String QUEUE_NAME = "activity.tracking";
    public static final String DLX_EXCHANGE_NAME = "dlx.exchange";
    public static final String DLQ_NAME = "activity.tracking.dlq";

    @Bean
    public TopicExchange activityExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public TopicExchange dlxExchange() {
        return new TopicExchange(DLX_EXCHANGE_NAME);
    }

    @Bean
    public Queue activityQueue() {
        return QueueBuilder.durable(QUEUE_NAME)
            .withArgument("x-dead-letter-exchange", DLX_EXCHANGE_NAME)
            .withArgument("x-dead-letter-routing-key", DLQ_NAME)
            .build();
    }

    @Bean
    public Queue dlq() {
        return QueueBuilder.durable(DLQ_NAME).build();
    }

    @Bean
    public Binding activityBinding() {
        return BindingBuilder
            .bind(activityQueue())
            .to(activityExchange())
            .with("group.#");
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder
            .bind(dlq())
            .to(dlxExchange())
            .with(DLQ_NAME);
    }
} 