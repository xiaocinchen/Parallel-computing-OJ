package com.offer.oj.controller;

import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.*;
import com.offer.oj.service.KaptchaService;
import com.offer.oj.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("/v1")
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private KaptchaService kaptchaService;

    @PostMapping("/register/send")
    public Result studentRegister(@RequestBody @Validated UserDTO userDTO, HttpServletResponse response) {
        userDTO.setRole("student");
        return userService.registerSendEmail(userDTO, response);
    }

    @GetMapping("/register/resend")
    public Result studentEmailResend(@CookieValue(value = "TEMP_LICENCE") Cookie cookie, @RequestParam String username) {
        if (cookie == null) {
            return new Result(false, "Cookie miss.", -3);
        }
        return userService.resendVerifyEmail(username, cookie.getValue());
    }

    @PostMapping("/login")
    public Result login(@CookieValue(value = "KAPTCHA") Cookie cookie, @RequestBody @Validated LoginDTO loginDTO, HttpServletResponse response) {
        if (ObjectUtils.isEmpty(cookie)) {
            return new Result(false, "Cookie miss.", -1);
        }
//        Result codeResult = kaptchaService.checkKaptcha(cookie.getValue(), loginDTO.getCode());
//        if (codeResult.getCode() != 0){
//            return codeResult;
//        }
        return userService.login(loginDTO, response);
    }

    @PostMapping("/login_1")
    public Result login_test(@RequestBody @Validated LoginDTO loginDTO, HttpServletResponse response) {
        return userService.login(loginDTO, response);
    }


    @RequestMapping("/register/verify")
    public Result verifyEmail(@RequestBody @Validated VerificationDTO verificationDTO) {
        return userService.registerVerifyEmail(verificationDTO);
    }

    @GetMapping("/logout")
    public Result logout(@CookieValue(required = false, value = "TOKEN") Cookie cookie) {
        return userService.logout(cookie);
    }

    @PostMapping("/password/forget")
    public Result forgetPassword(@RequestBody @Validated ForgetPasswordDTO forgetPasswordDTO) {
        return userService.forgetPassword(forgetPasswordDTO);
    }


    @PostMapping("/password/modify")
    public Result modifyPassword(@RequestBody @Validated ModifyPasswordDTO modifyPasswordDTO) {
        return userService.modifyPassword(modifyPasswordDTO);
    }
}
