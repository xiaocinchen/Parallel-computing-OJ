package com.offer.oj.dao.mapper;

import com.offer.oj.domain.OjQuestionGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OjQuestionGroupMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OjQuestionGroup row);

    int insertSelective(OjQuestionGroup row);

    OjQuestionGroup selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OjQuestionGroup row);

    int updateByPrimaryKey(OjQuestionGroup row);

    OjQuestionGroup selectIdByGroupName(@Param("group_name") String groupName);

    List<OjQuestionGroup> selectAllQuestionByTitle(@Param("group_name") String groupName);
}