package com.offer.oj.service;

import com.offer.oj.domain.dto.UserDTO;

import java.io.IOException;

public interface EmailService {

    void sendRegisterVerifyEmail(UserDTO userDTO);

    void sendModifyPasswordEmail(UserDTO userDTO);

}
