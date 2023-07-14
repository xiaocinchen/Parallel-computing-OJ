package com.offer.oj.service;

import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.QuestionDTO;
import com.offer.oj.domain.dto.VariableQuestionDTO;

import java.io.IOException;

public interface QuestionService {
    /**
     * 新增题目
     */
    Result addQuestion(VariableQuestionDTO variableQuestionDTO) throws IOException;

    boolean isValidUrl(String url) throws IOException;

    Result deleteQuestion(QuestionDTO questionDTO);

    Result modifyQuestion(VariableQuestionDTO questionDTO);
}
