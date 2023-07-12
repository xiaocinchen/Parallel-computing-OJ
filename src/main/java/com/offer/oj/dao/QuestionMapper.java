package com.offer.oj.dao;

import com.offer.oj.domain.OjQuestion;
import com.offer.oj.domain.dto.QuestionDTO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface QuestionMapper {
    void insertSelective(OjQuestion row);
    List<QuestionDTO> selectByTitle(String title);

    List<QuestionDTO> fuzzySelectByTitle(String title);
}
