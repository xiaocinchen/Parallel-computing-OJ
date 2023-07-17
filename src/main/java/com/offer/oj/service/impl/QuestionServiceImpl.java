package com.offer.oj.service.impl;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.offer.oj.dao.QuestionMapper;
import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.QuestionDTO;
import com.offer.oj.domain.dto.VariableQuestionDTO;
import com.offer.oj.domain.enums.CacheEnum;
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
import java.util.List;
import java.util.Objects;


@Slf4j
@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private CacheManager cacheManager;

    @Override
    public Result addQuestion(VariableQuestionDTO variableQuestionDTO) throws IOException {
        Result result = new Result();
        String message = "";
        if (!isValidUrl(variableQuestionDTO.getPictureUrl())) {
            message = "picture_url is invalid !" + variableQuestionDTO.getPictureUrl();
            log.error(message);
            result.setSuccess(false);
        }
        else {
            try {
                variableQuestionDTO.setModifier(variableQuestionDTO.getModifier());
                questionMapper.insertSelective(variableQuestionDTO);
                result.setSuccess(true);
                message = "Submit question successfully!";
                log.info(message);
            } catch (Exception e) {
                log.error(String.valueOf(e));
                result.setSuccess(false);
            }
        }
        result.setMessage(message);
        return result;
    }


    @Override
    public Result<List<QuestionDTO>> searchQuestion(String title) {
        Result<List<QuestionDTO>> result = new Result<>();
        Cache<String, List<QuestionDTO>> questionDTOCache = cacheManager.getCache(CacheEnum.SELECT_QUESTION_CACHE.getValue());
        if (!Objects.isNull(questionDTOCache.get(title))){
            List<QuestionDTO> questionDTO = questionDTOCache.get(title);
            System.out.println(questionDTO);
            result.setData(questionDTO);
            result.setSuccess(true);
        }
        else if (! ObjectUtils.isEmpty(questionMapper.fuzzySelectByTitle(title))) {
            List<QuestionDTO> questionDTOList = questionMapper.fuzzySelectByTitle(title);
            System.out.println(questionDTOList);
            Cache<String, List<QuestionDTO>> selectCache = cacheManager.getCache(CacheEnum.SELECT_QUESTION_CACHE.getValue());
            selectCache.put(title, questionDTOList);
            result.setSuccess(true);
            result.setData(questionDTOList);
        }
        else {
            result.setSuccess(false);
            result.setMessage("No related questions!");
        }
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
    public boolean isValidUrl(String url){
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
