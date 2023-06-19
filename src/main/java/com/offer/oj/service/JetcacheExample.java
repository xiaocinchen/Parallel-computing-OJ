package com.offer.oj.service;

import com.alicp.jetcache.anno.*;

import java.util.concurrent.TimeUnit;

/**
 * @Description: Jetcache使用示例，to be deleted
 * @Date: 2023/6/20 18:54
 * @Author: Xiaocinchen
 */
public interface JetcacheExample {
    // 在redis中查询是否存在userCache+#userId，不存在则存入返回值
    @CachePenetrationProtect
    @Cached(name = "userCache", key = "#userId", expire = 100, timeUnit = TimeUnit.SECONDS, cacheType = CacheType.REMOTE)
    String getUserById(Long userId);

    // 在redis中查询更新userCache+#Id的值为#name
    @CacheUpdate(name = "userCache", key = "#Id", value = "#name")
    void updateUser(Long Id, String name);

    //在redis中删除key为userCache+#Id的键值对
    @CacheInvalidate(name = "userCache", key = "#Id")
    void deleteUser(Long Id);
}
