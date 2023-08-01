package com.offer.oj.util.pack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisTemplateHolder {
    private static RedisTemplate<String, String> redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        RedisTemplateHolder.redisTemplate = redisTemplate;
    }

    public static RedisTemplate<String, String> getRedisTemplate() {
        return redisTemplate;
    }
}
