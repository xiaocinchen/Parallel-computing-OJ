package com.offer.oj.controller;


import com.offer.oj.dao.Result;
import com.offer.oj.service.UserService;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/info")
    @ResponseBody
    public Result info(@CookieValue(required = false, value = "TOKEN") Cookie cookie){
//        return userService.userInfo(cookie);
        return null;
    }
}
