package com.offer.oj.dao.mapper;

import com.offer.oj.domain.OjUser;

public interface OjUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OjUser row);

    int insertSelective(OjUser row);

    OjUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OjUser row);

    int updateByPrimaryKey(OjUser row);

    OjUser selectByUsername(String username);
}