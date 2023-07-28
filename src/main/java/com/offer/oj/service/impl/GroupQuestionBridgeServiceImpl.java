package com.offer.oj.service.impl;

import com.offer.oj.dao.GroupQuestionBridgeMapper;
import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.GroupQuestionDTO;
import com.offer.oj.service.GroupQuestionBridgeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GroupQuestionBridgeServiceImpl implements GroupQuestionBridgeService {
    @Autowired
    private GroupQuestionBridgeMapper groupQuestionBridgeMapper;
    @Override
    public Result addGroupQuestion(GroupQuestionDTO groupQuestionDTO) {
        Result result = new Result();
        try {
            groupQuestionBridgeMapper.insertSelctive(groupQuestionDTO);
        }catch (Exception e){
            log.error(String.valueOf(e));
            result.setSimpleResult(false, "Failed!", -1);
        }
        result.setSimpleResult(true, "Succeed！", 0);
        return result;
    }

    @Override
    public Result deleteQuestion(GroupQuestionDTO groupQuestionDTO) {
        String message = "";
        Result result = new Result<>();
        if (groupQuestionDTO == null || groupQuestionDTO.getId() == null) {
            message = "Lack parameters!";
            log.error(message + ": {}", groupQuestionDTO);
            result.setSimpleResult(false, message, -1);
        } else {
            if (groupQuestionBridgeMapper.selectByGroupId(groupQuestionDTO.getId()) == null) {
                message = "Delete failed: No such question, Id=" + groupQuestionDTO.getId();
                log.warn(message);
                result.setSimpleResult(false, message, -2);
            } else if (groupQuestionBridgeMapper.selectByGroupId(groupQuestionDTO.getId()).equals(null)) {
                message = "Delete question success. Id = " + groupQuestionDTO.getId();
                log.info(message);
                result.setSimpleResult(true, message, 0);
            } else {
                message = "Delete question failed. Id = " + groupQuestionDTO.getId();
                log.info(message);
                result.setSimpleResult(false, message, -3);
            }
        }
        return result;
    }
}
