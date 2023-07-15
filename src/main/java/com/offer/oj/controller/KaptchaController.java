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

    @PostMapping("kaptcha-image")
    @ResponseBody
    public Result getKaptchaImage(@RequestBody UserDTO userDTO) throws IOException {
        return kaptchaService.getKaptcha(userDTO);
    }
}

