package com.offer.oj.service;

import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.UserDTO;
import com.offer.oj.domain.dto.VerificationDTO;

public interface UserService {
    /**
     * 注册第一步：发验证邮件
     */
    Result registerSendEmail(UserDTO user, boolean isStudent);

    /**
     * 注册第二步：验证邮件
     */
    Result registerVerifyEmail(VerificationDTO verification);

    /**
     * 判断UserDTO是否为空
     */
    boolean isUserDTOEmpty(UserDTO userDTO);
}
