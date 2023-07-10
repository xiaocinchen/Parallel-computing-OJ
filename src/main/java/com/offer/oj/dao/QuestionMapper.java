package com.offer.oj.dao;

import com.offer.oj.domain.OjQuestion;

public interface QuestionMapper {
    void insertSelective(OjQuestion row);
}
