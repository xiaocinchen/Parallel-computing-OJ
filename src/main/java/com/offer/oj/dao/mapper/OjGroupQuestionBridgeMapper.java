package com.offer.oj.dao.mapper;

import com.offer.oj.domain.OjGroupQuestionBridge;

public interface OjGroupQuestionBridgeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OjGroupQuestionBridge row);

    int insertSelective(OjGroupQuestionBridge row);

    OjGroupQuestionBridge selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OjGroupQuestionBridge row);

    int updateByPrimaryKey(OjGroupQuestionBridge row);
}