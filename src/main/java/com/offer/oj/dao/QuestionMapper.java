package com.offer.oj.dao;

import com.offer.oj.domain.dto.QuestionDTO;
import com.offer.oj.domain.query.QuestionModifyQuery;

import java.util.List;

public interface QuestionMapper {
    Boolean insertSelective(QuestionDTO question);
    List<QuestionDTO> selectByTitle(String title);

    List<QuestionDTO> fuzzySelectByTitle(String title);


    Boolean deleteQuestionById(Integer questionId);

    QuestionDTO selectQuestionById(Integer questionId);

    Boolean modifyQuestion(QuestionModifyQuery question);
}
