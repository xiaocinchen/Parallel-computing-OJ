package com.offer.oj.dao.impl;

import com.offer.oj.dao.QuestionMapper;
import com.offer.oj.dao.mapper.OjQuestionMapper;
import com.offer.oj.domain.OjQuestion;
import com.offer.oj.domain.dto.QuestionDTO;
import com.offer.oj.domain.query.QuestionInnerQuery;
import com.offer.oj.domain.dto.VariableQuestionDTO;
import com.offer.oj.domain.query.QuestionModifyQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class QuestionMapperImpl implements QuestionMapper {
    @Autowired
    private OjQuestionMapper ojQuestionMapper;

    @Override
    public Boolean insertSelective(VariableQuestionDTO question) {
        OjQuestion ojQuestion = new OjQuestion();
        BeanUtils.copyProperties(question, ojQuestion);
        try {
            ojQuestionMapper.insertSelective(ojQuestion);
            return true;
        } catch (Exception e) {
            log.error("Insert Question Error, question: {}", question);
            return false;
        }
    }

    @Override
    public List<QuestionDTO> selectByTitle(String title) {
        List<OjQuestion> ojQuestionList = ojQuestionMapper.selectByTitle(title);
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (OjQuestion question : ojQuestionList){
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTOList.add(questionDTO);
        }
        return questionDTOList;
    }

    @Override
    public List<QuestionDTO> fuzzySelectByTitle(String title) {
        List<OjQuestion> ojQuestionList = ojQuestionMapper.selectAllQuestionByTitle(title);
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (OjQuestion question : ojQuestionList){
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTOList.add(questionDTO);
        }
        return questionDTOList;
    }

    @Override
    public Boolean deleteQuestionById(Integer questionId) {
        try {
            ojQuestionMapper.deleteByPrimaryKey(questionId);
            return true;
        } catch (Exception e) {
            log.error("Delete Question Error, questionId: {}", questionId);
            return false;
        }
    }

    @Override
    public QuestionDTO selectQuestionById(Integer questionId) {
        QuestionDTO question = new QuestionDTO();
        try {
            OjQuestion ojQuestion = ojQuestionMapper.selectByPrimaryKey(questionId);
            if (ojQuestion == null) {
                log.info("No such question, questionId: {}", questionId);
                return null;
            }
            BeanUtils.copyProperties(ojQuestion, question);
            return question;
        } catch (Exception e) {
            log.error("Query question error", e);
            return null;
        }
    }

    @Override
    public Boolean modifyQuestion(QuestionModifyQuery question) {
        if (question == null){
            log.warn("Modify Question Error, question is null.");
            return false;
        }
        try {
            OjQuestion ojQuestion = new OjQuestion();
            BeanUtils.copyProperties(question, ojQuestion);
            if (ojQuestionMapper.updateByPrimaryKeySelective(ojQuestion) == 1){
                log.info("Update Question Success.");
                return true;
            } else {
                log.warn("Update Question fail. QuestionId={}", question.getId());
                return false;
            }
        } catch (Exception e){
            log.error("Update Question Error.", e);
            return false;
        }
    }
}
