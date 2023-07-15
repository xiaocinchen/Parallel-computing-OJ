package com.offer.oj.dao;

import com.alicp.jetcache.anno.Cached;
import com.offer.oj.domain.dto.QuestionDTO;
import com.offer.oj.domain.dto.VariableQuestionDTO;
import com.offer.oj.domain.query.QuestionModifyQuery;

import java.util.List;

public interface QuestionMapper {
    Boolean insertSelective(VariableQuestionDTO question);

    List<QuestionDTO> selectByTitle(String title);

    List<QuestionDTO> fuzzySelectByTitle(String title);


    Boolean deleteQuestionById(Integer questionId);

    QuestionDTO selectQuestionById(Integer questionId);

    Boolean modifyQuestion(QuestionModifyQuery question);
}
