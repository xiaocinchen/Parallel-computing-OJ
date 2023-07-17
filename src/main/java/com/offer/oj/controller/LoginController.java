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
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/v1")
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/register/send")
    @ResponseBody
    public Result studentRegister(@RequestBody UserDTO userDTO) {
        userDTO.setRole("student");
        return userService.registerSendEmail(userDTO);
    }

    @PostMapping("/login")
    @ResponseBody
    public Result login(@RequestBody LoginDTO loginDTO, HttpServletResponse response){
        return userService.login(loginDTO, response);
    }

    @RequestMapping("/register/verify")
    @ResponseBody
    public Result verifyEmail(@RequestBody VerificationDTO verificationDTO) {
        return userService.registerVerifyEmail(verificationDTO);
    }

    @GetMapping("/logout")
    @ResponseBody
    public Result logout(@CookieValue(required = false, value = "TOKEN") Cookie cookie){
        return userService.logout(cookie);
    }
    @PostMapping("/forget-password")
    @ResponseBody
    public Result forgetPassword(@RequestBody ForgetPasswordDTO forgetPasswordDTO) throws IOException {
        return userService.forgetPassword(forgetPasswordDTO);
    }
}
