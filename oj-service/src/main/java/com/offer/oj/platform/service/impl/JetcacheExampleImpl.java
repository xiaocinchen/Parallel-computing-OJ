package com.offer.oj.platform.service.impl;

import com.alicp.jetcache.CacheManager;
import com.offer.oj.platform.service.JetcacheExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JetcacheExampleImpl implements JetcacheExample {

    //本地缓存执行方案
    @Autowired
    private CacheManager cacheManager;
//    private Cache<Long, String> userCache;

//    @PostConstruct
//    public void startCache(){
//        QuickConfig quickConfig = QuickConfig.newBuilder("userCache")
//                .expire(Duration.ofSeconds(100))
//                .cacheType(CacheType.BOTH)
//                .syncLocal(true)
//                .build();
//        userCache = cacheManager.getOrCreateCache(quickConfig);
//    }

    @Override
    public String getUserById(Long userId) {
        return "unknown1";
    }

    @Override
    public void updateUser(Long Id, String name) {
        System.out.println("updateUser:" + name + Id);
    }

    @Override
    public void deleteUser(Long Id) {
        System.out.println("deleteUser" + Id);
    }
}
