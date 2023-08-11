package com.offer.oj.service.impl;

import com.alibaba.fastjson.JSON;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.google.gson.Gson;
import com.offer.oj.MQ.sender.QuestionMQSender;
import com.offer.oj.dao.QuestionMapper;
import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.*;
import com.offer.oj.domain.enums.CacheEnum;
import com.offer.oj.domain.enums.RoleEnum;
import com.offer.oj.domain.query.QuestionModifyQuery;
import com.offer.oj.service.QuestionService;
import com.offer.oj.util.LockUtil;
import com.offer.oj.util.ThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;
import com.offer.oj.domain.enums.RoleEnum;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private QuestionMQSender questionMQSender;

    private static final String QUESTION_ADD_LOCK = "QUESTION_ADD_LOCK";

    @Override
    public Result addQuestion(VariableQuestionDTO variableQuestionDTO) {
        QuestionInsertDTO questionInsertDTO = new QuestionInsertDTO();
        String md5 = DigestUtils.md5DigestAsHex(JSON.toJSONString(variableQuestionDTO).getBytes());
        String lockKey = QUESTION_ADD_LOCK + md5;
        if (LockUtil.isLocked(lockKey, 10L)) {
            log.warn("Please do not resubmit.");
            return new Result(false, "Please do not resubmit.", -2);
        }
        Result result = new Result();
        String message = "";
        if (!isValidUrl(variableQuestionDTO.getPictureUrl())) {
            result.setSimpleResult(false, "picture_url is invalid !" + variableQuestionDTO.getPictureUrl(),-4);
        } else {
            try {
                if (Objects.nonNull(variableQuestionDTO.getDescription().getDescription())&&Objects.nonNull(variableQuestionDTO.getDescription().getExampleDTOList())){
                    String json = new Gson().toJson(variableQuestionDTO.getDescription());
                    BeanUtils.copyProperties(variableQuestionDTO, questionInsertDTO);
                    questionInsertDTO.setDescription(json);
                    questionMapper.insertSelective(questionInsertDTO);
                    result.setSimpleResult(true, "Submit question successfully!", 0);
                }
                else {
                    result.setSimpleResult(false, "Complete Description!", -3);
                }
            } catch (Exception e) {
                log.error(String.valueOf(e));
                result.setSuccess(false);
            }
        }
        return result;
    }


    @Override
    public Result<List<QuestionDTO>> searchQuestion(String title) {
        Result<List<QuestionDTO>> result = new Result<>();
        Cache<String, List<QuestionDTO>> questionDTOCache = cacheManager.getCache(CacheEnum.SELECT_QUESTION_CACHE.getValue());
        List<QuestionDTO> questionDTOList;
        if (Objects.nonNull(questionDTOCache.get(title))) {
            questionDTOList = questionDTOCache.get(title);
            result.setData(questionDTOList);
            result.setSuccess(true);
            result.setCode(0);
        } else if (!ObjectUtils.isEmpty(questionMapper.fuzzySelectByTitle(title))) {
            questionDTOList = questionMapper.fuzzySelectByTitle(title);
            Cache<String, List<QuestionDTO>> selectCache = cacheManager.getCache(CacheEnum.SELECT_QUESTION_CACHE.getValue());
            selectCache.put(title, questionDTOList);
            result.setSuccess(true);
            result.setCode(0);
            result.setData(questionDTOList);
        } else {
            result.setSuccess(false);
            result.setCode(-1);
            result.setMessage("No related questions!");
            return result;
        }
        ThreadPoolUtil.sendMQThreadPool.execute(() -> {
            questionDTOList.stream()
                    .parallel().map(QuestionDTO::getId)
                    .forEach(id -> questionMQSender.sendQuestionFuzzySearchMQ(id, title));
        });
        return result;
    }


    @Override
    @Transactional
    public Result deleteQuestion(QuestionDTO questionDTO) {
        String message = "";
        Result result = new Result<>();
        if (questionDTO == null || questionDTO.getId() == null) {
            message = "Lack parameters!";
            log.error(message + "question: {}", questionDTO);
            result.setSimpleResult(false, message, -1);
        } else {
            if (questionMapper.selectQuestionById(questionDTO.getId()) == null) {
                message = "Delete question failed: No such question, Id=" + questionDTO.getId();
                log.warn(message);
                result.setSimpleResult(false, message, -2);
            } else if (questionMapper.deleteQuestionById(questionDTO.getId())) {
                message = "Delete question success. Id = " + questionDTO.getId();
                log.info(message);
                result.setSimpleResult(true, message, 0);
                ThreadPoolUtil.sendMQThreadPool.execute(() -> questionMQSender.sendQuestionModifyMQ(questionDTO.getId()));
            } else {
                message = "Delete question failed. Id = " + questionDTO.getId();
                log.info(message);
                result.setSimpleResult(false, message, -3);
            }
        }
        return result;
    }

    @Override
    @Transactional
    public Result modifyQuestion(VariableQuestionDTO questionDTO) {
        String message = "";
        Result result = new Result();
        if (questionDTO == null) {
            message = "Lack parameters!";
            log.error(message + "question: {}", questionDTO);
            result.setSimpleResult(false, message, -1);
            return result;
        }
        QuestionModifyQuery questionModifyQuery = new QuestionModifyQuery();
        BeanUtils.copyProperties(questionDTO, questionModifyQuery);
        try {
            if (questionMapper.modifyQuestion(questionModifyQuery)) {
                message = "Modify question success.";
                result.setSimpleResult(true, message, 0);
                ThreadPoolUtil.sendMQThreadPool.execute(() -> questionMQSender.sendQuestionModifyMQ(questionModifyQuery.getId()));
            } else {
                message = "Modify question fail.";
                result.setSimpleResult(false, message, -2);
            }
            result.setSimpleResult(true, message);
            log.info(message + "Id = " + questionDTO.getId());
        } catch (Exception e) {
            throw new RuntimeException("Modify question Exception.");
        }
        return result;
    }

    @Override
    public Result<List<SearchResultDTO>> queryQuestionsByTitle(PageSearchDTO pageSearchDTO) {
        Result<List<SearchResultDTO>> result = new Result<>();
        List<SearchResultDTO> searchResultDTOList;
        try {
            if (Objects.isNull(pageSearchDTO.getRole())){
                pageSearchDTO.setRole(RoleEnum.STUDENT.getValue());
            }
            searchResultDTOList = questionMapper.queryQuestionsByTitle(pageSearchDTO);
        } catch (Exception e) {
            throw new RuntimeException("Query question exception");
        }
        result.setSimpleResult(true, 0);
        if (ObjectUtils.isEmpty(searchResultDTOList)) {
            result.setData(new ArrayList<>());
            return result;
        } else {
            result.setData(searchResultDTOList);
        }
        ThreadPoolUtil.sendMQThreadPool.execute(() -> {
            searchResultDTOList.stream()
                    .parallel().map(SearchResultDTO::getId)
                    .forEach(id -> questionMQSender.sendQuestionFuzzySearchMQ(id, pageSearchDTO.toString()));
        });
        return result;
    }


    /***** private method *****/
    private boolean isValidUrl(String url) {
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
    public VariableQuestionDTO questionDetail(Integer id){
        QuestionDTO questionDTO = questionMapper.selectQuestionById(id);
        VariableQuestionDTO variableQuestionDTO = new VariableQuestionDTO();
        QuestionDescriptionDTO questionDescriptionDTO = new QuestionDescriptionDTO();
        ExampleDTO exampleDTO = new ExampleDTO();
        List<ExampleDTO> exampleDTOList = new ArrayList<>();
        JsonNode examples;
        String description = questionDTO.getDescription();
        BeanUtils.copyProperties(questionDTO, variableQuestionDTO);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(description);
            questionDescriptionDTO.setDescription(jsonNode.get("description").asText());
            examples = jsonNode.get("exampleDTOList");
            if (ObjectUtils.isEmpty(examples)){
                questionDescriptionDTO.setExampleDTOList(Collections.emptyList());
            }else {
                for (JsonNode example: examples){
                    String input = example.get("input").asText();
                    String output = example.get("output").asText();
                    exampleDTO.setInput(input);
                    exampleDTO.setOutput(output);
                    exampleDTOList.add(exampleDTO);
                }
                questionDescriptionDTO.setExampleDTOList(exampleDTOList);
            }
            variableQuestionDTO.setDescription(questionDescriptionDTO);
        }catch (Exception e){
            throw new RuntimeException("Search question detail exception",e);
        }
        return variableQuestionDTO;
    }
}
