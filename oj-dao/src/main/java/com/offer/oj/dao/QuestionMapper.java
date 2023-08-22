package com.offer.oj.dao;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.offer.oj.domain.dto.*;
import com.offer.oj.domain.query.QuestionModifyQuery;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface QuestionMapper {
    Boolean insertSelective(QuestionInsertDTO question);

    List<QuestionDTO> fuzzySelectByTitle(String title);

    Boolean deleteQuestionById(Integer questionId);

    QuestionDTO selectQuestionById(Integer questionId);

    Boolean modifyQuestion(QuestionModifyQuery question);

    @Cached(name = "selectPageQuestionCache", key = "#pageSearchDTO.toString()", cacheNullValue = false, expire = 15, timeUnit = TimeUnit.DAYS, cacheType = CacheType.BOTH)
    List<SearchResultDTO> queryQuestionsByTitle(PageSearchDTO pageSearchDTO);
}
