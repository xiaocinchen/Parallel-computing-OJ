package com.offer.oj.controller;
import com.offer.oj.dao.Result;
import com.offer.oj.service.KaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("v1")
public class KaptchaController {
    @Autowired
    private KaptchaService kaptchaService;

    @GetMapping("kaptcha/image")
    @ResponseBody
    public void getKaptchaImage() throws IOException {
        kaptchaService.getKaptchaImage();
    }

    @PostMapping("kaptcha/check")
    public Result checkKaptcha(@RequestBody String code){

        return kaptchaService.checkKaptcha(code);
    }
}

