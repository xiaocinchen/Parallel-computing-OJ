package com.offer.oj.dao;

import com.alicp.jetcache.anno.Cached;
import com.offer.oj.domain.dto.QuestionDTO;
import com.offer.oj.domain.dto.VariableQuestionDTO;
import com.offer.oj.domain.query.QuestionModifyQuery;

public interface QuestionMapper {
    Boolean insertSelective(VariableQuestionDTO question);

    Boolean deleteQuestionById(Integer questionId);

    QuestionDTO selectQuestionById(Integer questionId);

    Boolean modifyQuestion(QuestionModifyQuery question);
}
