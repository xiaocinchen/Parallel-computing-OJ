package com.offer.oj.platform.service;

import com.offer.oj.domain.Result;
import com.offer.oj.domain.dto.QuestionGroupDTO;

public interface QuestionGroupService {

    Result addQuestionGroup(QuestionGroupDTO questionGroupDTO);

    Result searchQuestionGroup(String groupName);

    Result deleteQuestionGroup(QuestionGroupDTO questionGroupDTO);

    Result modifyQuestionGroup(QuestionGroupDTO questionGroupDTO);
}
