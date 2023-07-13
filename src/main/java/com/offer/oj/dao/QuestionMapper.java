package com.offer.oj.dao;

import com.offer.oj.domain.dto.QuestionDTO;

import java.util.List;

public interface QuestionMapper {
    Boolean insertSelective(QuestionDTO question);
    List<QuestionDTO> selectByTitle(String title);

    List<QuestionDTO> fuzzySelectByTitle(String title);


    Boolean deleteQuestionById(QuestionDTO question);

    QuestionDTO selectQuestionById(Integer questionId);
}
