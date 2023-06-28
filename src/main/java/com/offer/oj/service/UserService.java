package com.offer.oj.service;

import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.LoginDTO;
import com.offer.oj.domain.dto.UserDTO;

public interface UserService {
    /**
     * 注册
     */
    Result register(UserDTO user, boolean isStudent);

    /**
     * 判断UserDTO是否为空
     */
    boolean isUserDTOEmpty(UserDTO userDTO);

    /**
     * login
     */
    Result login(LoginDTO loginDTO);
}
