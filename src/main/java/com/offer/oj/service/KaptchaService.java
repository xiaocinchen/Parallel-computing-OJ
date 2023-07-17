package com.offer.oj.service;
import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.KaptchaDTO;
import com.offer.oj.domain.dto.VerificationDTO;

import java.io.IOException;

public interface KaptchaService {

    Result<KaptchaDTO> getKaptcha(String username) throws IOException;

    void saveKaptchaCode(String code, VerificationDTO verificationDTO);

    Result checkKaptcha(String code);

}
