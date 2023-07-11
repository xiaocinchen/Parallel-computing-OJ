package com.offer.oj.dao;

import com.offer.oj.domain.dto.QuestionDTO;

public interface QuestionMapper {
    Boolean insertSelective(QuestionDTO question);

    Boolean deleteQuestionById(QuestionDTO question);

    QuestionDTO selectQuestionById(Integer questionId);
}
