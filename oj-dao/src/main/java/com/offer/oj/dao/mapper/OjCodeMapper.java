package com.offer.oj.dao.mapper;

import com.offer.oj.domain.pojo.OjCode;
import com.offer.oj.domain.query.CodeInnerQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OjCodeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OjCode row);

    int insertSelective(OjCode row);

    OjCode selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OjCode row);

    int updateByPrimaryKey(OjCode row);

    int updateByFileName(OjCode row);

    List<OjCode> queryForList(CodeInnerQuery codeInnerQuery);

    List<OjCode> queryForSimpleList(CodeInnerQuery codeInnerQuery);
}