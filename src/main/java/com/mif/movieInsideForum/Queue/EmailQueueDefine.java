package com.mif.movieInsideForum.Queue;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailQueueDefine {

//    private final AmqpAdmin amqpAdmin;
    public static final String EMAIL_QUEUE = "emailQueue";
    public static final String EXCHANGE_NAME = "emailExchange";
    public static final String ROUTING_KEY = "emailRoutingKey";


    @Bean
    public Queue queue() {
        return new Queue(EMAIL_QUEUE, false);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    // cach thu cong
//    @Bean
//    public void declareEmailQueue() {
//        TopicExchange exchange = new TopicExchange(EXCHANGE_NAME);
//        amqpAdmin.declareExchange(exchange);
//
//        Queue queue = new Queue(EMAIL_QUEUE, true);
//        amqpAdmin.declareQueue(queue);
//
//        Binding binding = BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
//        amqpAdmin.declareBinding(binding);
//    }
}