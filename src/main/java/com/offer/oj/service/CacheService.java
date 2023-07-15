package com.offer.oj.service;

import com.alicp.jetcache.Cache;

public interface CacheService {
    Cache<Object, Object> getCache(String cacheEnum);
}
