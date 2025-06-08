package com.mif.movieInsideForum.Queue;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BadgeQueueDefine {
    public static final String BADGE_EXCHANGE = "badge.exchange";
    public static final String BADGE_EVALUATION_QUEUE = "badge.evaluation";
    public static final String BADGE_EVALUATION_ROUTING_KEY = "badge.evaluation";

    @Bean
    public DirectExchange badgeExchange() {
        return new DirectExchange(BADGE_EXCHANGE);
    }

    @Bean
    public Queue badgeEvaluationQueue() {
        return QueueBuilder.durable(BADGE_EVALUATION_QUEUE)
                .withArgument("x-dead-letter-exchange", "dlx.exchange")
                .withArgument("x-dead-letter-routing-key", "dlx.badge.evaluation")
                .build();
    }

    @Bean
    public Binding badgeEvaluationBinding(Queue badgeEvaluationQueue, DirectExchange badgeExchange) {
        return BindingBuilder.bind(badgeEvaluationQueue)
                .to(badgeExchange)
                .with(BADGE_EVALUATION_ROUTING_KEY);
    }
}