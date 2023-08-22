package com.offer.oj.dao;

import com.offer.oj.domain.dto.GroupQuestionDTO;
import com.offer.oj.domain.dto.QuestionGroupDTO;

public interface GroupQuestionBridgeMapper {

    Boolean insertSelctive(GroupQuestionDTO groupQuestionDTO);

    Boolean deleteItemById(Integer id);

    GroupQuestionDTO selectByGroupQuestionId(GroupQuestionDTO groupQuestionDTO);

    GroupQuestionDTO selectByGroupId(Integer id);
}
