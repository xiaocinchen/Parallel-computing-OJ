package com.offer.oj.service;

import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface UserService {
    /**
     * 注册第一步：发验证邮件
     */
    Result<String> registerSendEmail(UserDTO user);

    /**
     * 注册第二步：验证邮件
     */
    Result registerVerifyEmail(VerificationDTO verification);

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

    /**
     * forget password
     */
    Result forgetPassword(ForgetPasswordDTO forgetPasswordDTO);

    Boolean verifyRole(UserIdentityDTO userIdentityDTO, String role);

    UserIdentityDTO getUserIdentity(Integer userId);
}
