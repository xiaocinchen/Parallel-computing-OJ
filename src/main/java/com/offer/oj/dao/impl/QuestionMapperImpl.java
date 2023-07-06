package com.offer.oj.dao.impl;

import com.offer.oj.dao.QuestionMapper;
import com.offer.oj.dao.mapper.OjQuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QuestionMapperImpl implements QuestionMapper {
    @Autowired
    private OjQuestionMapper ojQuestionMapper;


}
