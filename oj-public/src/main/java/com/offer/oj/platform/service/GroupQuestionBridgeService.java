package com.offer.oj.platform.service;

import com.offer.oj.domain.Result;
import com.offer.oj.domain.dto.GroupQuestionDTO;

public interface GroupQuestionBridgeService {
    Result addGroupQuestion(GroupQuestionDTO groupQuestionDTO);

    Result  deleteQuestion(GroupQuestionDTO groupQuestionDTO);
}
