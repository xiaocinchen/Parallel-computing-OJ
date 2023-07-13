package com.offer.oj.dao.impl;

import com.offer.oj.dao.QuestionMapper;
import com.offer.oj.dao.mapper.OjQuestionMapper;
import com.offer.oj.domain.OjQuestion;
import com.offer.oj.domain.dto.QuestionDTO;
import com.offer.oj.domain.query.QuestionModifyQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
            ojQuestionMapper.updateByPrimaryKeySelective(ojQuestion);
            log.info("Update Question Success.");
            return true;
        } catch (Exception e){
            log.error("Update Question Error.", e);
            return false;
        }
    }
}
