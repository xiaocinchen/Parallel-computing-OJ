package com.offer.oj.dao.mapper;

import com.offer.oj.domain.OjQuestion;

public interface OjQuestionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OjQuestion row);

    int insertSelective(OjQuestion row);

    OjQuestion selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OjQuestion row);

    int updateByPrimaryKeyWithBLOBs(OjQuestion row);

    int updateByPrimaryKey(OjQuestion row);
}