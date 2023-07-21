package com.offer.oj.dao;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CacheUpdate;
import com.alicp.jetcache.anno.Cached;
import com.offer.oj.domain.dto.UserDTO;

import java.util.concurrent.TimeUnit;

public interface UserMapper {
    @Cached(name = "userCache", key = "#username", expire = 300, timeUnit = TimeUnit.SECONDS, cacheType = CacheType.REMOTE)
    UserDTO selectByUsername(String username);

    @Cached(name = "userCache", key = "#email", expire = 300, timeUnit = TimeUnit.SECONDS, cacheType = CacheType.REMOTE)
    UserDTO selectByEmail(String email);

    Integer selectIdByUsername(String username);

    UserDTO selectById(Integer id);

    void updateUserInfo(UserDTO userDTO);

}
