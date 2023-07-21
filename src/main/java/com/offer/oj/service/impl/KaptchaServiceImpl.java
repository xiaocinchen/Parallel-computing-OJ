package com.offer.oj.service.impl;

import com.alicp.jetcache.Cache;
import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.KaptchaDTO;
import com.offer.oj.domain.enums.CookieEnum;
import com.offer.oj.service.CacheService;
import com.offer.oj.service.KaptchaService;

import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.util.Objects;

import com.offer.oj.util.KaptchaUtil;
import com.offer.oj.util.TimeUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.offer.oj.domain.enums.CacheEnum;
import org.springframework.util.ObjectUtils;

import javax.imageio.ImageIO;

import static com.offer.oj.util.TimeUtil.getUniqueSequence;

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
    public Result checkKaptcha(String kaptchaToken, String code) {
        String cacheCode = (String) cacheService.getCache(CacheEnum.KAPTCHA_CACHE.getValue()).get(kaptchaToken);
        if (ObjectUtils.isEmpty(cacheCode)){
            return new Result(false, "Code expired.", -3);
        } else if(!cacheCode.equals(code)){
            return new Result(false, "Code wrong.", -4);
        } else{
            return new Result(true, "Check success!", 0);
        }
    }
}
