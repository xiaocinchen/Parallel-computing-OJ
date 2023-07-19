package com.offer.oj.controller;

import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.ForgetPasswordDTO;
import com.offer.oj.domain.dto.LoginDTO;
import com.offer.oj.domain.dto.UserDTO;
import com.offer.oj.domain.dto.VerificationDTO;
import com.offer.oj.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/v1")
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/register/send")
    public Result studentRegister(@RequestBody @Validated UserDTO userDTO) {
        userDTO.setRole("student");
        return userService.registerSendEmail(userDTO);
    }

    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO, HttpServletResponse response){
        return userService.login(loginDTO, response);
    }

    @RequestMapping("/register/verify")
    public Result verifyEmail(@RequestBody @Validated VerificationDTO verificationDTO) {
        return userService.registerVerifyEmail(verificationDTO);
    }

    @GetMapping("/logout")
    public Result logout(@CookieValue(required = false, value = "TOKEN") Cookie cookie){
        return userService.logout(cookie);
    }
    @PostMapping("/password/forget")
    public Result forgetPassword(@RequestBody @Validated ForgetPasswordDTO forgetPasswordDTO) throws IOException {
        return userService.forgetPassword(forgetPasswordDTO);
    }
}
