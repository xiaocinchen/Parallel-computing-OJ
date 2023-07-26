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
    /**
     * Email
     */
    @Bean("emailExchange")
    public Exchange emailExchange() {
        return ExchangeBuilder.topicExchange(MQExchangeEnum.EMAIL_EXCHANGE.getValue()).durable(true).build();
    }

    @Bean("emailQueue")
    public Queue emailQueue() {
        return QueueBuilder.durable(MQQueueEnum.EMAIL_QUEUE.getValue()).build();
    }

    @Bean
    public Binding emailQueueExchange(@Qualifier("emailQueue") Queue queue,
                                      @Qualifier("emailExchange") Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("email.#").noargs();
    }


    /**
     * Code
     */
    @Bean("codeExchange")
    public Exchange codeExchange() {
        return ExchangeBuilder.topicExchange(MQExchangeEnum.CODE_EXCHANGE.getValue()).durable(true).build();
    }

    @Bean("codeJudgeQueue")
    public Queue codeJudgeQueue() {
        return QueueBuilder.durable(MQQueueEnum.CODE_JUDGE_QUEUE.getValue()).build();
    }

    @Bean("codeResultQueue")
    public Queue codeResultQueue() {
        return QueueBuilder.durable(MQQueueEnum.CODE_RESULT_QUEUE.getValue()).build();
    }

    @Bean
    public Binding codeJudgeQueueExchange(@Qualifier("codeJudgeQueue") Queue queue,
                                          @Qualifier("codeExchange") Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("code.judge").noargs();
    }

    @Bean
    public Binding codeResultQueueExchange(@Qualifier("codeResultQueue") Queue queue,
                                           @Qualifier("codeExchange") Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("code.result").noargs();
    }


    /**
     * Question
     */
    @Bean("questionExchange")
    public Exchange questionExchange() {
        return ExchangeBuilder.topicExchange(MQExchangeEnum.QUESTION_EXCHANGE.getValue()).durable(true).build();
    }

    @Bean("questionFuzzyIdUpdateQueue")
    public Queue questionFuzzyIdUpdateQueue() {
        return QueueBuilder.durable(MQQueueEnum.QUESTION_FUZZY_ID_UPDATE_QUEUE.getValue()).build();
    }

    @Bean("questionFuzzyCacheUpdateQueue")
    public Queue questionFuzzyCacheUpdateQueue() {
        return QueueBuilder.durable(MQQueueEnum.QUESTION_FUZZY_CACHE_UPDATE_QUEUE.getValue()).build();
    }

    @Bean
    public Binding questionFuzzyIdQueueExchange(@Qualifier("questionFuzzyIdUpdateQueue") Queue queue,
                                                @Qualifier("questionExchange") Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("question.fuzzy.id").noargs();
    }

    @Bean
    public Binding questionFuzzyCacheQueueExchange(@Qualifier("questionFuzzyCacheUpdateQueue") Queue queue,
                                                   @Qualifier("questionExchange") Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("question.fuzzy.cache").noargs();
    }


    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}