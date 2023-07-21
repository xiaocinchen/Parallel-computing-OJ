package com.offer.oj.service;

import com.offer.oj.dao.Result;
import com.offer.oj.domain.OjUser;
import com.offer.oj.domain.dto.QuestionDTO;
import com.offer.oj.domain.dto.UserDTO;
import com.offer.oj.domain.dto.VariableQuestionDTO;
import jakarta.servlet.http.Cookie;

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
    Result<List<QuestionDTO>> searchQuestion(String title);

    boolean isQuestionDTOEmpty(QuestionDTO questionDTO);

    boolean isValidUrl(String url) throws IOException;

    Result deleteQuestion(QuestionDTO questionDTO);

    Result modifyQuestion(VariableQuestionDTO questionDTO);
}
