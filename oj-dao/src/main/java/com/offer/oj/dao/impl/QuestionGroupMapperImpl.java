package com.offer.oj.dao.impl;

import com.offer.oj.dao.QuestionGroupMapper;
import com.offer.oj.dao.mapper.OjQuestionGroupMapper;
import com.offer.oj.domain.pojo.OjQuestionGroup;
import com.offer.oj.domain.dto.QuestionGroupDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
@Slf4j
public class QuestionGroupMapperImpl implements QuestionGroupMapper {

    @Autowired
    private OjQuestionGroupMapper ojQuestionGroupMapper;

    @Override
    public boolean insertSelctive(QuestionGroupDTO questionGroupDTO) {
        OjQuestionGroup ojQuestionGroup = new OjQuestionGroup();
        BeanUtils.copyProperties(questionGroupDTO, ojQuestionGroup);
        try{
            ojQuestionGroupMapper.insertSelective(ojQuestionGroup);
            return true;
        }catch (Exception e){
            log.error("Insert Error!");
        }
        return false;
    }

    @Override
    public Integer selectIdByGroupName(String groupName) {
        OjQuestionGroup ojQuestionGroup = ojQuestionGroupMapper.selectIdByGroupName(groupName);
        return ojQuestionGroup.getId();
    }
    @Override
    public List<QuestionGroupDTO> fuzzySelectByName(String groupName){
        List<OjQuestionGroup> ojQuestionGroupList = ojQuestionGroupMapper.selectAllQuestionByTitle(groupName);
        List<QuestionGroupDTO> questionGroupDTOList = new ArrayList<>();
        for (OjQuestionGroup questionGroup : ojQuestionGroupList){
            QuestionGroupDTO questionGroupDTO = new QuestionGroupDTO();
            BeanUtils.copyProperties(questionGroup, questionGroupDTO);
            questionGroupDTOList.add(questionGroupDTO);
        }
        return questionGroupDTOList;
    }

    @Override
    public Boolean deleteGroupById(Integer groupId) {
        try {
            ojQuestionGroupMapper.deleteByPrimaryKey(groupId);
            return true;
        } catch (Exception e) {
            log.error("Delete Group Error, groupId: {}", groupId);
            return false;
        }
    }

    @Override
    public QuestionGroupDTO selectGroupById(Integer groupId) {
        QuestionGroupDTO questionGroup = new QuestionGroupDTO();
        try {
            OjQuestionGroup ojQuestionGroup = ojQuestionGroupMapper.selectByPrimaryKey(groupId);
            if (ojQuestionGroup == null) {
                log.info("No such group, groupId: {}", groupId);
                return null;
            }
            BeanUtils.copyProperties(ojQuestionGroup, questionGroup);
            return questionGroup;
        } catch (Exception e) {
            log.error("Query group error", e);
            return null;
        }
    }

    @Override
    public Boolean modifyGroup(QuestionGroupDTO questionGroup) {
        if (questionGroup == null){
            log.warn("Modify Group Error, Group is null.");
            return false;
        }
        try {
            OjQuestionGroup ojQuestionGroup = new OjQuestionGroup();
            BeanUtils.copyProperties(questionGroup, ojQuestionGroup);
            if (ojQuestionGroupMapper.updateByPrimaryKeySelective(ojQuestionGroup) == 1){
                log.info("Update Group Success.");
                return true;
            } else {
                log.warn("Update Group fail. GroupId={}", questionGroup.getId());
                return false;
            }
        } catch (Exception e){
            log.error("Update Group Error.", e);
            return false;
        }
    }
}
