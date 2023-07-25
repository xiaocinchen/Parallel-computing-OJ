package com.offer.oj.dao.impl;

import com.offer.oj.dao.QuestionGroupMapper;
import com.offer.oj.domain.dto.GroupQuestionDTO;

public class QuestionGroupMapperImpl implements QuestionGroupMapper {
    @Override
    public boolean insertSelctive(GroupQuestionDTO groupQuestionDTO) {
        return false;
    }
}
