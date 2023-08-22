package com.offer.oj.dao;

import com.offer.oj.domain.dto.GroupQuestionDTO;
import com.offer.oj.domain.dto.QuestionDTO;
import com.offer.oj.domain.dto.QuestionGroupDTO;
import org.springframework.cache.annotation.CachePut;

import java.util.List;

public interface QuestionGroupMapper {

    boolean insertSelctive(QuestionGroupDTO questionGroupDTO);

    Integer selectIdByGroupName(String groupName);

    List<QuestionGroupDTO> fuzzySelectByName(String groupName);

    Boolean deleteGroupById(Integer groupId);

    QuestionGroupDTO selectGroupById(Integer groupId);

    Boolean modifyGroup(QuestionGroupDTO questionGroupDTO);
}
