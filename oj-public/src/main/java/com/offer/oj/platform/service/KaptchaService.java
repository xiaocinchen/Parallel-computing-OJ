package com.offer.oj.platform.service;
import com.offer.oj.domain.Result;
import jakarta.servlet.http.HttpServletResponse;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

public interface KaptchaService {

    BufferedImage getKaptchaImage(HttpServletResponse response);

    Map<String, Object> getKaptchaImage() throws IOException;

    Result checkKaptcha(String kaptchaToken, String code);
}
