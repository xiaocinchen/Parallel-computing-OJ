package com.offer.oj.dao.mapper;

import com.offer.oj.domain.OjQuestionGroup;

public interface OjQuestionGroupMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OjQuestionGroup row);

    int insertSelective(OjQuestionGroup row);

    OjQuestionGroup selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OjQuestionGroup row);

    int updateByPrimaryKey(OjQuestionGroup row);
}