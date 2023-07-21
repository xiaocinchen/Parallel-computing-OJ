package com.offer.oj.service;
import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.KaptchaDTO;

import java.io.IOException;

public interface KaptchaService {

    Result<KaptchaDTO> getKaptcha();

    void getKaptchaImage() throws IOException;

    void saveKaptchaCode(String code);

    Result checkKaptcha(String code);
}
