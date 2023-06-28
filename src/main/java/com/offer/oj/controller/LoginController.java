package com.offer.oj.controller;

import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.LoginDTO;
import com.offer.oj.domain.dto.UserDTO;
import com.offer.oj.service.UserService;
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

    @RequestMapping("index")
    public String index(String name, Model model) {
        model.addAttribute("name", name);
        return "index";
    }

    @GetMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO){

        System.out.println(loginDTO);
        return userService.login(loginDTO);
    }

}
