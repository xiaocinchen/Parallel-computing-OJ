package com.offer.oj.dao.impl;

import com.offer.oj.dao.GroupQuestionBridgeMapper;
import com.offer.oj.dao.mapper.OjGroupQuestionBridgeMapper;
import com.offer.oj.domain.OjGroupQuestionBridge;
import com.offer.oj.domain.OjQuestion;
import com.offer.oj.domain.OjQuestionGroup;
import com.offer.oj.domain.dto.GroupQuestionDTO;
import com.offer.oj.domain.dto.QuestionGroupDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GroupQuestionBridgeMapperImpl implements GroupQuestionBridgeMapper {
    @Autowired
    private OjGroupQuestionBridgeMapper ojGroupQuestionBridgeMapper;
    @Override
    public Boolean insertSelctive(GroupQuestionDTO groupQuestionDTO) {
        OjGroupQuestionBridge ojGroupQuestionBridge = new OjGroupQuestionBridge();
        BeanUtils.copyProperties(groupQuestionDTO, ojGroupQuestionBridge);
        try{
            ojGroupQuestionBridgeMapper.insertSelective(ojGroupQuestionBridge);
            return true;
        }catch (Exception e){
            log.error("Insert Error!");
        }
        return false;
    }

    @Override
    public Boolean deleteItemById(Integer id) {
        try {
            ojGroupQuestionBridgeMapper.deleteByPrimaryKey(id);
            return true;
        } catch (Exception e) {
            log.error("Delete Question Error, questionId: {}", id);
            return false;
        }
    }

    @Override
    public GroupQuestionDTO selectByGroupQuestionId(GroupQuestionDTO groupQuestionDTO) {
        OjGroupQuestionBridge ojGroupQuestionBridge = new OjGroupQuestionBridge();
        BeanUtils.copyProperties(groupQuestionDTO, ojGroupQuestionBridge);
        ojGroupQuestionBridge = ojGroupQuestionBridgeMapper.selectByGroupQuestionId(groupQuestionDTO.getGroupId(), groupQuestionDTO.getQuestionId());
        BeanUtils.copyProperties(ojGroupQuestionBridge, groupQuestionDTO);
        return groupQuestionDTO;

    }

    @Override
    public GroupQuestionDTO selectByGroupId(Integer id) {
        return null;
    }

//    @Override
//    public GroupQuestionDTO selectByQuestionId(Integer id) {
//        GroupQuestionDTO groupQuestionDTO = new GroupQuestionDTO();
//        try {
//            OjGroupQuestionBridge ojGroupQuestionBridge = ojGroupQuestionBridgeMapper.selectByPrimaryKey(id);
//            if (ojGroupQuestionBridge == null) {
//                log.info("No such question, questionId: {}", id);
//                return null;
//            }
//            BeanUtils.copyProperties(ojGroupQuestionBridge, groupQuestionDTO);
//            return questionGroup;
//        } catch (Exception e) {
//            log.error("Query group error", e);
//            return null;
//        }
//    }
}
