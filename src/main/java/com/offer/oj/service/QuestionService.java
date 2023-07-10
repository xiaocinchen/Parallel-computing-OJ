package com.offer.oj.service;

import com.offer.oj.dao.Result;
import com.offer.oj.domain.OjUser;
import com.offer.oj.domain.dto.QuestionDTO;
import com.offer.oj.domain.dto.UserDTO;
import jakarta.servlet.http.Cookie;

import java.io.IOException;

public interface QuestionService {
    /**
     * 新增题目
     */
    Result<String> addQuestion(OjUser user, QuestionDTO questionDTO) throws IOException;

    boolean isQuestionDTOEmpty(QuestionDTO questionDTO);

    boolean isValidUrl(String url) throws IOException;
}
