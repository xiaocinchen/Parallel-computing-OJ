package com.offer.oj.dao.mapper;

import com.offer.oj.domain.pojo.OjUser;
import com.offer.oj.domain.query.UserInnerQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OjUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OjUser row);

    int insertSelective(OjUser row);

    OjUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OjUser row);

    int updateByPrimaryKey(OjUser row);

    int updateByUsername(OjUser row);

    List<OjUser> queryForList(UserInnerQuery userInnerQuery);

    Integer queryForCount(UserInnerQuery userInnerQuery);

}