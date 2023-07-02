package com.offer.oj.service;

import com.offer.oj.domain.dto.UserDTO;

public interface EmailService {
    void sendRegisterVerifyEmail(UserDTO userDTO);

}
