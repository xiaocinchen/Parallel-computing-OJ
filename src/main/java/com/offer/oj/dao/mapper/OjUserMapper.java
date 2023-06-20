package com.offer.oj.dao.mapper;

import com.offer.oj.domain.OjUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OjUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OjUser record);

    int insertSelective(OjUser record);

    OjUser selectByPrimaryKey(Integer id);

    OjUser selectByUsername(String username);

    int updateByPrimaryKeySelective(OjUser record);

    int updateByPrimaryKey(OjUser record);
}