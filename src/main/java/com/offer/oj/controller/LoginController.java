package com.offer.oj.controller;

import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.UserDTO;
import com.offer.oj.domain.dto.VerificationDTO;
import com.offer.oj.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
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

    @RequestMapping("index")
    public String index(String name, Model model) {
        model.addAttribute("name", name);
        return "index";
    }

    @RequestMapping("/register/verify")
    @ResponseBody
    public Result verifyEmail(@RequestBody VerificationDTO verificationDTO){
        return userService.registerVerifyEmail(verificationDTO);
    }
}
