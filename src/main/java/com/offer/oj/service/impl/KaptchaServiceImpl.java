package com.offer.oj.service.impl;

import com.alicp.jetcache.Cache;
import com.google.code.kaptcha.Producer;
import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.KaptchaDTO;
import com.offer.oj.service.KaptchaService;

import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.io.IOException;
import java.util.Objects;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import com.alicp.jetcache.CacheManager;
import org.springframework.stereotype.Service;
import com.offer.oj.domain.enums.CacheEnum;

import javax.imageio.ImageIO;

import static com.offer.oj.util.TimeUtil.getUniqueSequence;

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
    public Result<KaptchaDTO> getKaptcha() {
        String kaptchaText = producer.createText();
        log.info("******************当前验证码为：{}******************", kaptchaText);
        BufferedImage kaptchaImage = producer.createImage(kaptchaText);
        KaptchaDTO kaptchaDTO = new KaptchaDTO(kaptchaText, kaptchaImage);
        saveKaptchaCode(kaptchaText);
        Result<KaptchaDTO> kaptchaDTOResult = new Result<>();
        kaptchaDTOResult.setData(kaptchaDTO);
        kaptchaDTOResult.setSuccess(true);
        return kaptchaDTOResult;
    }

    @Override
    public void getKaptchaImage() throws IOException {
        String kaptchaText = producer.createText();
        log.info("******************当前验证码为：{}******************", kaptchaText);
        BufferedImage kaptchaImage = producer.createImage(kaptchaText);
        this.response.setContentType("image/jpeg");
        try {
            ServletOutputStream out = this.response.getOutputStream();
            // 向页面输出验证码s
            ImageIO.write(kaptchaImage, "jpg", out);
            // 清空缓存区
            out.flush();
            // 关闭输出流
            out.close();
        } catch (Exception e) {
            throw new ImagingOpException("Output Image Exception.");
        }
        saveKaptchaCode(kaptchaText);
    }

    @Override
    public void saveKaptchaCode(String code) {
        Result<String> result = new Result<>();
        String message = "";
        String kaptchaCode = getUniqueSequence();
        Cache<String, String> kaptchaCache = cacheManager.getCache(CacheEnum.KAPTCHA_CACHE.getValue());
        kaptchaCache.put(code, kaptchaCode);
        result.setSuccess(true);
        message = "save kaptcha success!";
        log.info(message);
    }

    @Override
    public Result checkKaptcha(String code) {
        Result<String> result = new Result<>();
        String message = "";
        Cache<String, String> kaptchaDTOCache = cacheManager.getCache(CacheEnum.KAPTCHA_CACHE.getValue());
        String kaptcha = kaptchaDTOCache.get(code);
        if (Objects.isNull(kaptcha)) {
            message = "kaptcha error!";
            log.error(message);
            result.setCode(-1);
            result.setSimpleResult(false, message);

        }
        else {
            message = "kaptcha success!";
            log.info(message);
            result.setCode(0);
            result.setSimpleResult(true, message);
            kaptchaDTOCache.remove(kaptcha);
        }
        return result;
    }
}
