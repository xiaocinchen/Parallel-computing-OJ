package com.offer.oj.dao.impl;

import com.offer.oj.dao.UserMapper;
import com.offer.oj.dao.mapper.OjUserMapper;
import com.offer.oj.domain.OjUser;
import com.offer.oj.domain.dto.UserDTO;
import com.offer.oj.domain.query.UserInnerQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapperImpl implements UserMapper {
    @Autowired
    private OjUserMapper ojUserMapper;

    public UserDTO selectByUsername(String username) {
        UserInnerQuery userInnerQuery = new UserInnerQuery();
        userInnerQuery.setUsername(username);
        List<UserDTO> userDTOList = getUserDTO((userInnerQuery));
        return userDTOList.isEmpty() ? null : userDTOList.get(0);
    }

    public UserDTO selectByEmail(String email){
        UserInnerQuery userInnerQuery = new UserInnerQuery();
        userInnerQuery.setEmail(email);
        List<UserDTO> userDTOList = getUserDTO((userInnerQuery));
        return userDTOList.isEmpty() ? null : userDTOList.get(0);
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
