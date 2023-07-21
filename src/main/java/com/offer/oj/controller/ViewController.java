package com.offer.oj.controller;

import com.offer.oj.service.UserService;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@CrossOrigin
@RequestMapping("/view")
public class ViewController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login(@CookieValue(required = false, value = "TOKEN")Cookie cookie){
        if(userService.isLogin(cookie)){
            return "redirect:index";
        }
        return "login";
    }

    @GetMapping("/index")
    public String index(@CookieValue(required = false, value = "TOKEN") Cookie cookie) {
        if(userService.isLogin(cookie)){
            return "index";
        }else{
            return "redirect:login";
        }
    }

    @GetMapping("/register")
    public String register(){
        return "register";
    }
}