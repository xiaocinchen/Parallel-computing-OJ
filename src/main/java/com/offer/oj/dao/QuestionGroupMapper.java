package com.offer.oj.dao;

import com.offer.oj.domain.dto.GroupQuestionDTO;

public interface QuestionGroupMapper {

    boolean insertSelctive(GroupQuestionDTO groupQuestionDTO);
}
