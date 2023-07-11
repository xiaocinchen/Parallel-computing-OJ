package com.offer.oj.dao;

import com.offer.oj.domain.OjQuestion;
import com.offer.oj.domain.dto.QuestionDTO;

public interface QuestionMapper {
    void insertSelective(OjQuestion row);
}
