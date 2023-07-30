package com.offer.oj.service;

import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.GroupQuestionDTO;

public interface GroupQuestionBridgeService {
    Result addGroupQuestion(GroupQuestionDTO groupQuestionDTO);

    Result  deleteQuestion(GroupQuestionDTO groupQuestionDTO);
}
