package com.offer.oj.dao.mapper;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.offer.oj.domain.OjUser;
import com.offer.oj.domain.query.UserInnerQuery;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface OjUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OjUser row);

    int insertSelective(OjUser row);

    OjUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OjUser row);

    int updateByPrimaryKey(OjUser row);

    List<OjUser> queryForList(UserInnerQuery userInnerQuery);

    Integer queryForCount(UserInnerQuery userInnerQuery);

}