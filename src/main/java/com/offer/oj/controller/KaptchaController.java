package com.offer.oj.controller;
import com.google.code.kaptcha.Producer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.awt.image.BufferedImage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletOutputStream;
import javax.imageio.ImageIO;



@Slf4j
@Controller
@RequestMapping("kaptcha")
public class KaptchaController {
    @Autowired
    private Producer producer;

    @GetMapping("kaptcha-image")
    public void getKaptchaImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        String kaptchaText = producer.createText();
        log.info("******************当前验证码为：{}******************", kaptchaText);
        BufferedImage kaptchaImage = producer.createImage(kaptchaText);
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
    }
}
