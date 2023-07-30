package com.offer.oj.service;

import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.GroupQuestionDTO;
import com.offer.oj.domain.dto.QuestionDTO;
import com.offer.oj.domain.dto.QuestionGroupDTO;

public interface QuestionGroupService {

    Result addQuestionGroup(QuestionGroupDTO questionGroupDTO);

    Result searchQuestionGroup(String groupName);

    Result deleteQuestionGroup(QuestionGroupDTO questionGroupDTO);

    Result modifyQuestionGroup(QuestionGroupDTO questionGroupDTO);
}
