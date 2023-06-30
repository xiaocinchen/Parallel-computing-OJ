package com.offer.oj.service;

import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.LoginDTO;
import com.offer.oj.domain.dto.UserDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

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
    Result login(LoginDTO loginDTO, HttpServletResponse response);

    /**
     * isLogin
     */
    boolean isLogin(Cookie cookie);

    /**
     * logout
     */
    Result logout(Cookie cookie);

    /**
     * userInfo
     */
    Result userInfo(Cookie cookie);

}
