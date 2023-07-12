package com.offer.oj.service;

import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.QuestionDTO;
import java.io.IOException;
import java.util.List;

public interface QuestionService {
    /**
     * 新增题目
     */
    Result<String> addQuestion(Integer useId, QuestionDTO questionDTO) throws IOException;

    /**
    * 查找题目
    */
    List<QuestionDTO> selectQuestion(String title);

    boolean isQuestionDTOEmpty(QuestionDTO questionDTO);

    boolean isValidUrl(String url) throws IOException;
}
