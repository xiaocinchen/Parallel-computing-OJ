package com.offer.oj.controller;

import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.LoginDTO;
import com.offer.oj.domain.dto.UserDTO;
import com.offer.oj.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @ResponseBody
    public Result register(@RequestBody UserDTO userDTO) {
        return userService.register(userDTO, true);
    }

    @PostMapping("/login")
    @ResponseBody
    public Result login(@RequestBody LoginDTO loginDTO, HttpServletResponse response){
        return userService.login(loginDTO, response);
    }

    @GetMapping("/logout")
    @ResponseBody
    public Result logout(@CookieValue(required = false, value = "TOKEN") Cookie cookie){
        return userService.logout(cookie);
    }
}
