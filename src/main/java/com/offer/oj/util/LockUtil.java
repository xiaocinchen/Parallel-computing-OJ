package com.offer.oj.util;

import com.offer.oj.util.pack.RedisTemplateHolder;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

public class LockUtil {
    public static boolean isLocked(String key, Long timeoutSecond) {
        RedisTemplate<String, String> redisTemplate = RedisTemplateHolder.getRedisTemplate();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            return true;
        } else {
            redisTemplate.opsForValue().set(key, String.valueOf(timeoutSecond));
            redisTemplate.expire(key, timeoutSecond, TimeUnit.SECONDS);
            return false;
        }
    }
}
