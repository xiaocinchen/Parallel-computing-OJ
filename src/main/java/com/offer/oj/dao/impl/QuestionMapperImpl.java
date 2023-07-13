package com.offer.oj.dao.impl;

import com.offer.oj.dao.QuestionMapper;
import com.offer.oj.dao.mapper.OjQuestionMapper;
import com.offer.oj.domain.OjQuestion;
import com.offer.oj.domain.dto.QuestionDTO;
import com.offer.oj.domain.query.QuestionInnerQuery;
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
    public Boolean insertSelective(QuestionDTO question) {
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
        QuestionInnerQuery questionInnerQuery = new QuestionInnerQuery();
        questionInnerQuery.setTitle(title);
        List<QuestionDTO> questionDTOList = getQuestionDTO(questionInnerQuery);
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

    private List<QuestionDTO> getQuestionDTO(QuestionInnerQuery questionInnerQuery){
        List<OjQuestion> ojQuestionList = ojQuestionMapper.queryForList(questionInnerQuery);
        if (CollectionUtils.isEmpty(ojQuestionList)) {
            return Collections.emptyList();
        } else {
            return ojQuestionList.stream().filter(ojQuestion -> !ObjectUtils.isEmpty(ojQuestion)).map(ojQuestion -> {
                QuestionDTO questionDTO = new QuestionDTO();
                BeanUtils.copyProperties(ojQuestion, questionDTO);
                return questionDTO;
            }).collect(Collectors.toList());
        }
    }

    @Override
    public Boolean deleteQuestionById(QuestionDTO question) {
        Integer questionId = question.getQuestionId();
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
}
