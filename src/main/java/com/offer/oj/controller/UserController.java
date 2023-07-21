package com.offer.oj.controller;


import com.offer.oj.dao.Result;
import com.offer.oj.service.UserService;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/info")
    @ResponseBody
    public Result info(@CookieValue(required = false, value = "TOKEN") Cookie cookie){
        return userService.userInfo(cookie);
    }
}
