package com.offer.oj.platform.service.impl;

import com.offer.oj.common.util.JsonUtils;
import com.offer.oj.domain.Result;
import com.offer.oj.domain.dto.KaptchaDTO;
import com.offer.oj.domain.enums.CookieEnum;
import com.offer.oj.common.service.CacheService;
import com.offer.oj.platform.service.KaptchaService;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.offer.oj.common.util.KaptchaUtil;
import com.offer.oj.common.util.TimeUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.offer.oj.domain.enums.CacheEnum;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
public class KaptchaServiceImpl implements KaptchaService {
    @Autowired
    private CacheService cacheService;


    @Override
    public BufferedImage getKaptchaImage(HttpServletResponse response) {
        KaptchaDTO kaptcha = KaptchaUtil.getKaptcha();
        response.setContentType("image/jpeg");
        String kaptchaToken = TimeUtil.getUniqueSequence();
        cacheService.getCache(CacheEnum.KAPTCHA_CACHE.getValue()).put(kaptchaToken, kaptcha.getCode());
        Cookie cookie = new Cookie(CookieEnum.KAPTCHA.getName(), kaptchaToken);
        response.addCookie(cookie);
        return kaptcha.getImage();
    }

    @Override
    public Map<String, Object> getKaptchaImage() {
        KaptchaDTO kaptcha = KaptchaUtil.getKaptcha();
        String kaptchaToken = TimeUtil.getUniqueSequence();
        cacheService.getCache(CacheEnum.KAPTCHA_CACHE.getValue()).put(kaptchaToken, kaptcha.getCode());
        Map<String, Object> map = new HashMap<>();
        map.put("cookieName", CookieEnum.KAPTCHA.getName());
        map.put("cookieValue", kaptchaToken);
        BufferedImage image = kaptcha.getImage();
        try {
            map.put("kaptcha", JsonUtils.imageToJsonBytes(image, "png"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    @Override
    public Result checkKaptcha(String kaptchaToken, String code) {
        String cacheCode = (String) cacheService.getCache(CacheEnum.KAPTCHA_CACHE.getValue()).get(kaptchaToken);
        if (ObjectUtils.isEmpty(cacheCode)) {
            return new Result(false, "Code expired.", -2);
        } else if (!cacheCode.equals(code)) {
            return new Result(false, "Code wrong.", -3);
        } else {
            return new Result(true, "Check success!", 0);
        }
    }
}
