package com.offer.oj.common.service;

import com.alicp.jetcache.Cache;

public interface CacheService {
    Cache<Object, Object> getCache(String cacheEnum);
}
