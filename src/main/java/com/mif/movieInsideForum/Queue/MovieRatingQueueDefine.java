package com.mif.movieInsideForum.Queue;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MovieRatingQueueDefine {

    public static final String MOVIE_RATING_QUEUE = "movie.rating.events";
    public static final String EXCHANGE_NAME = "movieRatingExchange";
    public static final String ROUTING_KEY = "movieRatingRoutingKey";

    @Bean
    public Queue movieRatingQueue() {
        return new Queue(MOVIE_RATING_QUEUE, true);
    }

    @Bean
    public TopicExchange movieRatingExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding movieRatingBinding(Queue movieRatingQueue, TopicExchange movieRatingExchange) {
        return BindingBuilder.bind(movieRatingQueue).to(movieRatingExchange).with(ROUTING_KEY);
    }
} 