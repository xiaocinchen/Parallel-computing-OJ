package com.offer.oj.service;

import com.offer.oj.domain.dto.EmailDTO;

public interface EmailService {
    void sendVerifyEmail(EmailDTO emailDTO);

}
