package com.offer.oj.service;

import com.offer.oj.domain.dto.EmailDTO;
import com.offer.oj.domain.dto.UserDTO;

public interface EmailService {
    void sendRegisterVerifyEmail(EmailDTO emailDTO);

}
