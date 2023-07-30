package com.offer.oj.dao.mapper;

import com.offer.oj.domain.OjGroupQuestionBridge;
import org.apache.ibatis.annotations.Param;

public interface OjGroupQuestionBridgeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OjGroupQuestionBridge row);

    int insertSelective(OjGroupQuestionBridge row);

    OjGroupQuestionBridge selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OjGroupQuestionBridge row);

    int updateByPrimaryKey(OjGroupQuestionBridge row);

    OjGroupQuestionBridge selectByGroupQuestionId(@Param("questionGroupId")Integer groupId, @Param("questionId") Integer questionId);
}