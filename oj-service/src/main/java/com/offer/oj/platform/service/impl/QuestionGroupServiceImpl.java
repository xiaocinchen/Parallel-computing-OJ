package com.offer.oj.platform.service.impl;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.offer.oj.dao.QuestionGroupMapper;
import com.offer.oj.domain.Result;
import com.offer.oj.domain.dto.QuestionGroupDTO;
import com.offer.oj.domain.enums.CacheEnum;
import com.offer.oj.platform.service.QuestionGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Objects;


@Slf4j
@Service
public class QuestionGroupServiceImpl implements QuestionGroupService {
    @Autowired
    private QuestionGroupMapper questionGroupMapper;

    @Autowired
    private CacheManager cacheManager;

    @Override
    public Result addQuestionGroup(QuestionGroupDTO questionGroupDTO) {
        Result result = new Result();
        try {
            questionGroupMapper.insertSelctive(questionGroupDTO);
        }catch (Exception e){
            log.error(String.valueOf(e));
            result.setSimpleResult(false, "Failed!", -1);
        }
        result.setSimpleResult(true, "Succeed！", 0);
        return result;
    }

    @Override
    public Result<List<QuestionGroupDTO>> searchQuestionGroup(String groupName) {
        Result<List<QuestionGroupDTO>> result = new Result<>();
        Cache<String, List<QuestionGroupDTO>> questionDTOCache = cacheManager.getCache(CacheEnum.SELECT_QUESTION_GROUP_CACHE.getValue());
        if (!Objects.isNull(questionDTOCache.get(groupName))){
            List<QuestionGroupDTO> questionGroupDTOList = questionDTOCache.get(groupName);
            result.setSimpleResult(true, "SUCCEED", 0, questionGroupDTOList);
        }else if (! ObjectUtils.isEmpty(questionGroupMapper.fuzzySelectByName(groupName))) {
            List<QuestionGroupDTO> questionGroupDTOList = questionGroupMapper.fuzzySelectByName(groupName);
            Cache<String, List<QuestionGroupDTO>> selectCache = cacheManager.getCache(CacheEnum.SELECT_QUESTION_GROUP_CACHE.getValue());
            selectCache.put(groupName, questionGroupDTOList);
            result.setSimpleResult(true, "SUCCEED", 0, questionGroupDTOList);
        }
        else {
            result.setSimpleResult(false, "FAILED", -1);
        }
        return result;
    }

    @Override
    public Result deleteQuestionGroup(QuestionGroupDTO questionGroupDTO) {
        String message = "";
        Result result = new Result<>();
        if (questionGroupDTO == null) {
            result.setSimpleResult(false, "Lack parameters!", -1);
        } else {
            if (questionGroupMapper.selectGroupById(questionGroupDTO.getId()) == null) {
                message = "Delete group failed: No such group, Id=" + questionGroupDTO.getGroupName();
                log.warn(message);
                result.setSimpleResult(false, message, -2);
            } else if (questionGroupMapper.deleteGroupById(questionGroupDTO.getId())) {
                message = "Delete question success. Id = " + questionGroupDTO.getId();
                log.info(message);
                result.setSimpleResult(true, message, 0);
            } else {
                message = "Delete question failed. Id = " + questionGroupDTO.getId();
                log.info(message);
                result.setSimpleResult(false, message, -3);
            }
        }
        return result;
    }

    @Override
    public Result modifyQuestionGroup(QuestionGroupDTO questionGroupDTO) {
        String message = "";
        Result result = new Result();
        if (questionGroupDTO == null) {
            message = "Lack parameters!";
            log.error(message + "question: {}", questionGroupDTO);
            result.setSimpleResult(false, message);
            return result;
        }
        try {
            if (questionGroupMapper.modifyGroup(questionGroupDTO)) {
                message = "Modify Group success.";
            } else {
                message = "Modify Group fail.";
            }
            result.setSimpleResult(true, message);
            log.info(message + "Id = " + questionGroupDTO.getId());
        } catch (Exception e) {
            throw new RuntimeException("Modify group Exception.");
        }
        return result;
    }
}
