package com.offer.oj.dao.impl;

import com.offer.oj.dao.UserMapper;
import com.offer.oj.dao.mapper.OjUserMapper;
import com.offer.oj.domain.pojo.OjUser;
import com.offer.oj.domain.dto.UserDTO;
import com.offer.oj.domain.enums.CacheEnum;
import com.offer.oj.domain.query.UserInnerQuery;
import com.offer.oj.common.service.CacheService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapperImpl implements UserMapper {
    @Autowired
    private OjUserMapper ojUserMapper;

    @Autowired
    private CacheService cacheService;

    @Override
    public UserDTO selectByUsername(String username) {
        UserInnerQuery userInnerQuery = new UserInnerQuery();
        userInnerQuery.setUsername(username);
        List<UserDTO> userDTOList = getUserDTO((userInnerQuery));
        return userDTOList.isEmpty() ? null : userDTOList.get(0);
    }

    @Override
    public UserDTO selectByEmail(String email) {
        UserInnerQuery userInnerQuery = new UserInnerQuery();
        userInnerQuery.setEmail(email);
        List<UserDTO> userDTOList = getUserDTO((userInnerQuery));
        return userDTOList.isEmpty() ? null : userDTOList.get(0);
    }

    @Override
    public Integer selectIdByUsername(String username) {
        UserInnerQuery userInnerQuery = new UserInnerQuery();
        userInnerQuery.setUsername(username);
        List<OjUser> ojUserList = ojUserMapper.queryForList(userInnerQuery);
        if (CollectionUtils.isEmpty(ojUserList)) {
            return null;
        } else {
            return ojUserList.get(0).getId();
        }
    }

    @Override
    public UserDTO selectById(Integer id) {
        UserInnerQuery userInnerQuery = new UserInnerQuery();
        userInnerQuery.setId(id);
        List<UserDTO> userDTOList = getUserDTO((userInnerQuery));
        if (CollectionUtils.isEmpty(userDTOList)) {
            return null;
        } else {
            return userDTOList.get(0);
        }
    }

    @Override
    public void updateUserInfo(UserDTO userDTO) {
        OjUser ojUser = new OjUser();
        BeanUtils.copyProperties(userDTO, ojUser);
        try {
            ojUserMapper.updateByUsername(ojUser);
            cacheService.getCache(CacheEnum.USER_CACHE.getValue()).remove(userDTO.getUsername());
            cacheService.getCache(CacheEnum.USER_CACHE.getValue()).remove(userDTO.getEmail());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<UserDTO> getUserDTO(UserInnerQuery userInnerQuery) {
        List<OjUser> ojUserList = ojUserMapper.queryForList(userInnerQuery);
        if (CollectionUtils.isEmpty(ojUserList)) {
            return Collections.emptyList();
        } else {
            return ojUserList.stream().filter(ojUser -> !ObjectUtils.isEmpty(ojUser)).map(ojUser -> {
                UserDTO userDTO = new UserDTO();
                BeanUtils.copyProperties(ojUser, userDTO);
                return userDTO;
            }).collect(Collectors.toList());
        }
    }
}
