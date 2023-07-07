package com.offer.oj.controller;
import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.UserDTO;
import com.offer.oj.service.KaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("kaptcha")
public class KaptchaController {
    @Autowired
    private KaptchaService kaptchaService;

    @GetMapping("kaptcha-image")
    public Result getKaptchaImage(@RequestParam UserDTO userDTO) throws IOException {
        return kaptchaService.getKaptchaImage(userDTO);
    }
}

