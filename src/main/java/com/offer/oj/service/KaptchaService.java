package com.offer.oj.service;
import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.KaptchaDTO;
import jakarta.servlet.http.HttpServletResponse;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface KaptchaService {

    BufferedImage getKaptchaImage(HttpServletResponse response);

    Result checkKaptcha(String kaptchaToken, String code);
}
