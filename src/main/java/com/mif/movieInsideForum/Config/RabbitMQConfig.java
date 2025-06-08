package com.mif.movieInsideForum.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
@Configuration
public class RabbitMQConfig {
    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }
//    @Bean
//    public MessageConverter messageConverter() {
//        SimpleMessageConverter converter = new SimpleMessageConverter();
//        converter.setAllowedListPatterns(Collections.singletonList("com.mif.movieInsideForum.*")); // chỉ định các package được phép gửi message
//        return converter;
//    }

//    @Bean
//    public MessageConverter messageConverter() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.activateDefaultTyping(
//                BasicPolymorphicTypeValidator.builder()
//                        .allowIfSubType("com.mif.movieInsideForum")
//                        .build(),
//                ObjectMapper.DefaultTyping.NON_FINAL
//        );
//        return new Jackson2JsonMessageConverter(objectMapper);
//    }




}