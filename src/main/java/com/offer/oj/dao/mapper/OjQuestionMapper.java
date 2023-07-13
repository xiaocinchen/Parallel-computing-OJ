package com.offer.oj.dao.mapper;

import com.offer.oj.domain.OjQuestion;
import com.offer.oj.domain.OjUser;
import com.offer.oj.domain.query.QuestionInnerQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OjQuestionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OjQuestion row);

    int insertSelective(OjQuestion row);

    OjQuestion selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OjQuestion row);

    int updateByPrimaryKeyWithBLOBs(OjQuestion row);

    int updateByPrimaryKey(OjQuestion row);

    List<OjQuestion> queryForList(QuestionInnerQuery questionInnerQuery);

    Integer queryForCount(QuestionInnerQuery questionInnerQuery);
    List<OjQuestion> selectAllQuestionByTitle(@Param("title") String title);
}