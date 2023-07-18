package com.offer.oj.config;

import com.offer.oj.domain.enums.MQExchangeEnum;
import com.offer.oj.domain.enums.MQQueueEnum;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    //交换机名称
    //声明交换机
    @Bean("emailExchange")
    public Exchange emailExchange(){
        return ExchangeBuilder.topicExchange(MQExchangeEnum.EMAIL_EXCHANGE.getValue()).durable(true).build();
    }

    @Bean("codeExchange")
    public Exchange codeExchange(){
        return ExchangeBuilder.topicExchange(MQExchangeEnum.CODE_EXCHANGE.getValue()).durable(true).build();
    }

    //声明队列
    @Bean("emailQueue")
    public Queue emailQueue(){
        return QueueBuilder.durable(MQQueueEnum.EMAIL_QUEUE.getValue()).build();
    }

    @Bean("codeQueue")
    public Queue codeQueue(){
        return QueueBuilder.durable(MQQueueEnum.CODE_QUEUE.getValue()).build();
    }

    //绑定队列和交换机
    @Bean
    public Binding emailQueueExchange(@Qualifier("emailQueue") Queue queue,
                                      @Qualifier("emailExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("email.#").noargs();
    }

    @Bean
    public Binding codeQueueExchange(@Qualifier("codeQueue") Queue queue,
                                     @Qualifier("codeExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("code.#").noargs();
    }


    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}