package com.offer.oj.dao.mapper;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.offer.oj.domain.OjUser;

import java.util.concurrent.TimeUnit;

public interface OjUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OjUser row);

    int insertSelective(OjUser row);

    OjUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OjUser row);

    int updateByPrimaryKey(OjUser row);

    @Cached(name = "userCache", key = "#username", expire = 300, timeUnit = TimeUnit.SECONDS, cacheType = CacheType.REMOTE)
    OjUser selectByUsername(String username);

    @Cached(name = "userCache", key = "#email", expire = 300, timeUnit = TimeUnit.SECONDS, cacheType = CacheType.REMOTE)
    OjUser selectByEmail(String email);
}