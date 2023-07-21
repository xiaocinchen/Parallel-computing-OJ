package com.offer.oj.service.impl;

import com.alicp.jetcache.Cache;
import com.google.code.kaptcha.Producer;
import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.KaptchaDTO;
import com.offer.oj.domain.dto.VerificationDTO;
import com.offer.oj.service.KaptchaService;

import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.util.Objects;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import com.alicp.jetcache.CacheManager;
import org.springframework.stereotype.Service;
import com.offer.oj.domain.enums.CacheEnum;

import javax.imageio.ImageIO;

@Slf4j
@Service
public class KaptchaServiceImpl implements KaptchaService {
    @Autowired
    private Producer producer;
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private HttpServletResponse response;

    @Override
    public Result<KaptchaDTO> getKaptcha(String username) {
        String kaptchaText = producer.createText();
        log.info("******************当前验证码为：{}******************", kaptchaText);
        BufferedImage kaptchaImage = producer.createImage(kaptchaText);
        KaptchaDTO kaptchaDTO = new KaptchaDTO(kaptchaText, kaptchaImage);
        response.setContentType("image/jpeg");
        try {
            ServletOutputStream out = response.getOutputStream();
            // 向页面输出验证码s
            ImageIO.write(kaptchaImage, "jpg", out);
            // 清空缓存区
            out.flush();
            // 关闭输出流
            out.close();
        } catch (Exception e) {
            throw new ImagingOpException("Output Image Exception.");
        }
        VerificationDTO verificationDTO = new VerificationDTO();
        verificationDTO.setUsername(username);
        verificationDTO.setCode(kaptchaText);
        saveKaptchaCode(kaptchaText, verificationDTO);
        Result<KaptchaDTO> kaptchaDTOResult = new Result<>();
        kaptchaDTOResult.setData(kaptchaDTO);
        kaptchaDTOResult.setSuccess(true);
        return kaptchaDTOResult;
    }

    @Override
    public void saveKaptchaCode(String code, VerificationDTO verificationDTO) {
        Result<String> result = new Result<>();
        String message = "";
        Cache<String, VerificationDTO> kaptchaCache = cacheManager.getCache(CacheEnum.KAPTCHA_CACHE.getValue());
        kaptchaCache.put(code, verificationDTO);
        result.setSuccess(true);
        message = "save kaptcha success!";
        log.info(message);
    }

    @Override
    public Result checkKaptcha(String code) {
        Result<String> result = new Result<>();
        String message = "";
        Cache<String, VerificationDTO> kaptchaDTOCache = cacheManager.getCache(CacheEnum.KAPTCHA_CACHE.getValue());
        VerificationDTO kaptcha = kaptchaDTOCache.get(code);
        if (Objects.isNull(kaptcha)) {
            message = "kaptcha error!";
            log.error(message);
            result.setSimpleResult(false, message);

        }
        message = "kaptcha success!";
        log.info(message);
        result.setSimpleResult(true, message);
        return result;
    }
}
