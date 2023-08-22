package com.offer.oj.platform.service;

import com.offer.oj.domain.dto.EmailDTO;

public interface EmailService {
    void sendVerifyEmail(EmailDTO emailDTO);

    void sendForgetVerifyEmail(EmailDTO emailDTO);
}
