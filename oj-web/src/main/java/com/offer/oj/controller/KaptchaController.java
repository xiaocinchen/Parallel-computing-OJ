package com.offer.oj.controller;

import com.offer.oj.domain.Result;
import com.offer.oj.platform.service.KaptchaService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/v1")
public class KaptchaController {

    @Autowired
    private KaptchaService kaptchaService;

    @GetMapping("/kaptcha/image")
    public void getKaptchaImage(HttpServletResponse response) {
        BufferedImage kaptchaImage = kaptchaService.getKaptchaImage(response);
        try {
            ServletOutputStream out = response.getOutputStream();
            ImageIO.write(kaptchaImage, "jpg", out);
            out.flush();
            out.close();
        } catch (Exception e) {
            throw new ImagingOpException("Output Image Exception.");
        }
    }

    @GetMapping("/kaptcha/check")
    public Result checkKaptcha(@CookieValue(value = "KAPTCHA") Cookie cookie, @RequestParam String code) {
        if (ObjectUtils.isEmpty(cookie)){
            return new Result(false, "Cookie miss.", -1);
        } else if (ObjectUtils.isEmpty(code)){
            return new Result(false,"Code miss", -2);
        }
        return kaptchaService.checkKaptcha(cookie.getValue(), code);
    }
}

