package com.offer.oj.MQ.listener;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.offer.oj.domain.dto.QuestionSynDTO;
import com.offer.oj.domain.enums.CacheEnum;
import com.offer.oj.service.CacheService;
import com.offer.oj.util.FrequencySet;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class QuestionMQListener {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CacheService cacheService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "question_fuzzy_id_update_queue", durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = "question_exchange", type = ExchangeTypes.TOPIC),
            key = "question.fuzzy.id"), ackMode = "MANUAL")
    @RabbitHandler
    public void listenQuestionFuzzyIdMQ(@Payload QuestionSynDTO questionSynDTO, @Headers Map<String, Object> headers, Channel channel) {
        long deliveryTag = (long) headers.get(AmqpHeaders.DELIVERY_TAG);
        Integer id = questionSynDTO.getId();
        String cacheKey = questionSynDTO.getCacheKey();
        Cache<Integer, FrequencySet<String>> cache = cacheManager.getCache(CacheEnum.QUESTION_ID_FUZZY_KEY_CACHE.getValue());
        cache.computeIfAbsent(id, key -> FrequencySet.of(cacheKey)).add(cacheKey);
        try {
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "question_fuzzy_cache_update_queue", durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = "question_exchange", type = ExchangeTypes.TOPIC),
            key = "question.fuzzy.cache"), ackMode = "MANUAL")
    @RabbitHandler
    public void listenQuestionFuzzyCacheMQ(@Payload Integer id, @Headers Map<String, Object> headers, Channel channel) {
        long deliveryTag = (long) headers.get(AmqpHeaders.DELIVERY_TAG);
        Cache<Integer, FrequencySet<String>> cache = cacheManager.getCache(CacheEnum.QUESTION_ID_FUZZY_KEY_CACHE.getValue());
        FrequencySet<String> frequencySet = cache.get(id);
        if (frequencySet != null) {
            frequencySet.parallelStream().forEach(key -> cacheService.getCache(CacheEnum.PAGE_QUESTION_CACHE.getValue()).remove(key));
            cache.remove(id);
        }
        try {
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
