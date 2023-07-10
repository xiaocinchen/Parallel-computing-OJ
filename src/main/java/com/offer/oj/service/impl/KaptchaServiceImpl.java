package com.offer.oj.service.impl;
import com.alicp.jetcache.Cache;
import com.google.code.kaptcha.Producer;
import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.KaptchaDTO;
import com.offer.oj.domain.dto.UserDTO;
import com.offer.oj.service.KaptchaService;
import java.awt.image.BufferedImage;
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

@Slf4j
@Service
public class KaptchaServiceImpl implements KaptchaService {
    @Autowired
    private Producer producer;
    @Autowired
    private CacheManager cacheManager;

    private Cache<String, UserDTO> kaptchaDTOCache;

    @Autowired
    private HttpServletResponse response;

    @Override
    public Result<KaptchaDTO> getKaptchaImage(UserDTO userDTO) throws IOException {
        String kaptchaText = producer.createText();
        log.info("******************当前验证码为：{}******************", kaptchaText);
        BufferedImage kaptchaImage = producer.createImage(kaptchaText);
        KaptchaDTO kaptchaDTO = new KaptchaDTO(kaptchaText, kaptchaImage);
        response.setContentType("image/jpeg");
        ServletOutputStream out = response.getOutputStream();
        // 向页面输出验证码s
        ImageIO.write(kaptchaImage, "jpg", out);
        try {
            // 清空缓存区
            out.flush();
        } finally {
            // 关闭输出流
            out.close();
        }
        saveKaptchaCode(kaptchaDTO, userDTO);
        Result<KaptchaDTO> kaptchaDTOResult = new Result<>();
        kaptchaDTOResult.setData(kaptchaDTO);
        kaptchaDTOResult.setSuccess(true);
        return kaptchaDTOResult;
    }

    @Override
    public void saveKaptchaCode(KaptchaDTO kaptchaDTO, UserDTO userDTO) {
        Result<String> result = new Result<>();
        String message = "";
        Cache<String, UserDTO> kaptchaCache = cacheManager.getCache(CacheEnum.KAPTCHA_CACHE.getValue());
        kaptchaCache.put(kaptchaDTO.getCode(), userDTO);
        result.setSuccess(true);
        message = "save kaptcha success!";
        log.info(message);
    }

    @Override
    public Result checkKaptcha(String code) {
        Result<String> result = new Result<>();
        String message = "";
        kaptchaDTOCache = cacheManager.getCache(CacheEnum.KAPTCHA_CACHE.getValue());
        UserDTO kaptcha = kaptchaDTOCache.get(code);
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
