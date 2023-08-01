package com.offer.oj.service;

import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.PageSearchDTO;
import com.offer.oj.domain.dto.QuestionDTO;
import com.offer.oj.domain.dto.SearchResultDTO;
import com.offer.oj.domain.dto.VariableQuestionDTO;

import java.io.IOException;
import java.util.List;

public interface QuestionService {
    /**
     * 新增题目
     */
    Result addQuestion(VariableQuestionDTO variableQuestionDTO);

    /**
     * 查找题目
     */
    @Deprecated
    Result<List<QuestionDTO>> searchQuestion(String title);

    boolean isQuestionDTOEmpty(QuestionDTO questionDTO);

    boolean isValidUrl(String url) throws IOException;

    Result deleteQuestion(QuestionDTO questionDTO);

    Result modifyQuestion(VariableQuestionDTO questionDTO);

    Result<List<SearchResultDTO>> queryQuestionsByTitle(PageSearchDTO pageSearchDTO);
}
