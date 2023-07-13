package com.offer.oj.dao;

import com.offer.oj.domain.dto.QuestionDTO;
import com.offer.oj.domain.query.QuestionModifyQuery;

public interface QuestionMapper {
    Boolean insertSelective(QuestionDTO question);

    Boolean deleteQuestionById(Integer questionId);

    QuestionDTO selectQuestionById(Integer questionId);

    Boolean modifyQuestion(QuestionModifyQuery question);
}
