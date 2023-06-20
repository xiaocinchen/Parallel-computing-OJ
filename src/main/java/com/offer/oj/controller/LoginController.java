package com.offer.oj.controller;

import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.UserDTO;
import com.offer.oj.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/v1")
public class LoginController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @ResponseBody
    public Result register(@RequestBody UserDTO userDTO){
        return userService.register(userDTO, true);
    }
}
