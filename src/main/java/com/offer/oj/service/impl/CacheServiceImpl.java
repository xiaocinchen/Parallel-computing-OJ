package com.offer.oj.service.impl;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.template.QuickConfig;
import com.offer.oj.domain.enums.CacheEnum;
import com.offer.oj.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;

@Service
public class CacheServiceImpl implements CacheService {

    @Autowired
    private CacheManager cacheManager;

    @PostConstruct
    public void initRegisterCache() {
        QuickConfig quickConfig = QuickConfig.newBuilder(CacheEnum.REGISTER_CACHE.getValue())
                .expire(Duration.ofMinutes(5))
                .localExpire(Duration.ofSeconds(60))
                .cacheType(CacheType.BOTH) // two level cache
                .syncLocal(true) // invalidate local cache in all jvm process after update
                .build();
        cacheManager.getOrCreateCache(quickConfig);
    }

    @PostConstruct
    public void initUserCache() {
        QuickConfig quickConfig = QuickConfig.newBuilder(CacheEnum.USER_CACHE.getValue())
                .expire(Duration.ofMinutes(5))
                .localExpire(Duration.ofSeconds(60))
                .cacheType(CacheType.BOTH)
                .syncLocal(true)
                .build();
        cacheManager.getOrCreateCache(quickConfig);
    }

    @PostConstruct
    public void initKaptchaCache() {
        QuickConfig quickConfig = QuickConfig.newBuilder(CacheEnum.KAPTCHA_CACHE.getValue())
                .expire(Duration.ofMinutes(5))
                .localExpire(Duration.ofSeconds(60))
                .cacheType(CacheType.BOTH)
                .syncLocal(true)
                .build();
        cacheManager.getOrCreateCache(quickConfig);
    }

    @PostConstruct
    public void initSelectQuestionCache() {
        QuickConfig quickConfig = QuickConfig.newBuilder(CacheEnum.SELECT_QUESTION_CACHE.getValue())
                .expire(Duration.ofDays(15))
                .localExpire(Duration.ofDays(10))
                .cacheType(CacheType.BOTH)
                .syncLocal(true)
                .build();
        cacheManager.getOrCreateCache(quickConfig);
    }

    @PostConstruct
    public void initLoginCache() {
        QuickConfig quickConfig = QuickConfig.newBuilder(CacheEnum.LOGIN_CACHE.getValue())
//                .expire(Duration.ofDays(1))
                .localExpire(Duration.ofHours(12))
                .cacheType(CacheType.LOCAL)
                .syncLocal(true)
                .build();
        cacheManager.getOrCreateCache(quickConfig);
    }

    @PostConstruct
    public void initStageCodeCache() {
        QuickConfig quickConfig = QuickConfig.newBuilder(CacheEnum.STAGE_CODE_CACHE.getValue())
                .expire(Duration.ofDays(30))
                .localExpire(Duration.ofHours(12))
                .cacheType(CacheType.BOTH)
                .syncLocal(true)
                .build();
        cacheManager.getOrCreateCache(quickConfig);
    }

    @PostConstruct
    public void initQuestionFuzzyCache(){
        QuickConfig quickConfig = QuickConfig.newBuilder(CacheEnum.QUESTION_ID_FUZZY_KEY_CACHE.getValue())
                .expire(Duration.ofDays(15))
                .localExpire(Duration.ofDays(10))
                .cacheType(CacheType.BOTH)
                .syncLocal(true)
                .build();
        cacheManager.getOrCreateCache(quickConfig);
    }

    private QuickConfig getDefaultQuickConfig(String cacheEnum) {
        return QuickConfig.newBuilder(cacheEnum)
                .localExpire(Duration.ofMinutes(30))
                .cacheType(CacheType.LOCAL)
                .syncLocal(true)
                .build();
    }


    @Override
    public Cache<Object, Object> getCache(String cacheEnum) {
        return CacheEnum.getValues().contains(cacheEnum) ? cacheManager.getOrCreateCache(getDefaultQuickConfig(cacheEnum)) : null;
    }
}
