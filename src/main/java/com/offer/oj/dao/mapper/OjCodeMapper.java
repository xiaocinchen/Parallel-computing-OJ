package com.offer.oj.dao.mapper;

import com.offer.oj.domain.OjCode;

public interface OjCodeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OjCode row);

    int insertSelective(OjCode row);

    OjCode selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OjCode row);

    int updateByPrimaryKey(OjCode row);
}