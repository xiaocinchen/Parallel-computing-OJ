package com.offer.oj.service.impl;

import com.offer.oj.dao.QuestionMapper;
import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.QuestionDTO;
import com.offer.oj.domain.dto.VariableQuestionDTO;
import com.offer.oj.domain.query.QuestionModifyQuery;
import com.offer.oj.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;


@Slf4j
@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public Result addQuestion(QuestionDTO questionDTO) throws IOException {
        Result result = new Result();
        String message = "";

        if (isQuestionDTOEmpty(questionDTO)) {
            message = "Incomplete question information";
            log.error(message);
        } else if (questionDTO.getTitle().length() < 2 || questionDTO.getTitle().length() > 25) {
            message = "The title should be between 2 and 25 characters! " + questionDTO.getTitle();
            log.error(message);
        } else if (questionDTO.getDescription().length() < 20 || questionDTO.getDescription().length() > 200) {
            message = "description should be between 20 and 100 characters!";
            log.error(message);
        } else if (!isValidUrl(questionDTO.getPictureUrl())) {
            message = "picture_url is invalid !" + questionDTO.getPictureUrl();
            log.error(message);
        }
        if (message.isEmpty()) {
            try {
                questionDTO.setModifier(questionDTO.getUsername());
                questionMapper.insertSelective(questionDTO);
                result.setSuccess(true);
                message = "Submit question successfully!";
                log.info(message);
            } catch (Exception e) {
                log.error(String.valueOf(e));
                result.setSuccess(false);
            }
        } else {
            result.setSuccess(false);
        }
        result.setMessage(message);
        return result;
    }


    @Override
    public boolean isQuestionDTOEmpty(QuestionDTO questionDTO) {
        return Objects.isNull(questionDTO)
                || ObjectUtils.isEmpty(questionDTO.getTitle())
                || ObjectUtils.isEmpty(questionDTO.getDescription())
                || ObjectUtils.isEmpty(questionDTO.getPictureUrl());
    }

    @Override
    public boolean isValidUrl(String url) throws IOException {
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        }
        if (uri.getHost() == null) {
            return false;
        }
        if (uri.getScheme().equalsIgnoreCase("http") || uri.getScheme().equalsIgnoreCase("https")) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public Result deleteQuestion(QuestionDTO questionDTO) {
        String message = "";
        Result result = new Result<>();
        if (questionDTO == null || questionDTO.getId() == null) {
            message = "Lack parameters!";
            log.error(message + "question: {}", questionDTO);
            result.setSimpleResult(false, message);
        } else {
            if (questionMapper.selectQuestionById(questionDTO.getId()) == null) {
                message = "Delete question failed: No such question, Id=" + questionDTO.getId();
                log.warn(message);
                result.setSimpleResult(false, message);
            } else if (questionMapper.deleteQuestionById(questionDTO.getId())) {
                message = "Delete question success. Id = " + questionDTO.getId();
                log.info(message);
                result.setSimpleResult(true, message);
            } else {
                message = "Delete question failed. Id = " + questionDTO.getId();
                log.info(message);
                result.setSimpleResult(false, message);
            }
        }
        return result;
    }

    @Override
    @Transactional
    public Result modifyQuestion(VariableQuestionDTO questionDTO) {
        String message = "";
        Result result = new Result();
        if (questionDTO == null || questionDTO.getId() == null) {
            message = "Lack parameters!";
            log.error(message + "question: {}", questionDTO);
            result.setSimpleResult(false, message);
            return result;
        }
        QuestionModifyQuery questionModifyQuery = new QuestionModifyQuery();
        BeanUtils.copyProperties(questionDTO, questionModifyQuery);
        try {
            if (questionMapper.modifyQuestion(questionModifyQuery)) {
                message = "Modify question success.";
            } else {
                message = "Modify question fail.";
            }
            result.setSimpleResult(true, message);
            log.info(message + "Id = " + questionDTO.getId());
        } catch (Exception e) {
            throw new RuntimeException("Modify question Exception.");
        }
        return result;
    }
}
