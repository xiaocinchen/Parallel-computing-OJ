package com.offer.oj.platform.service;
import com.offer.oj.domain.Result;
import jakarta.servlet.http.HttpServletResponse;

import java.awt.image.BufferedImage;

public interface KaptchaService {

    BufferedImage getKaptchaImage(HttpServletResponse response);

    Result checkKaptcha(String kaptchaToken, String code);
}
