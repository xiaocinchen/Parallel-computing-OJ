package com.offer.oj.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class LockUtil {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    public boolean isLocked(String key, Long timeoutSecond) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            return true;
        } else {
            redisTemplate.opsForValue().set(key, String.valueOf(timeoutSecond));
            redisTemplate.expire(key, timeoutSecond, TimeUnit.SECONDS);
            return false;
        }
    }
}
