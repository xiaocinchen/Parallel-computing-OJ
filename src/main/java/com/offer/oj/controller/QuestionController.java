package com.offer.oj.controller;

import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.QuestionDTO;
import com.offer.oj.domain.enums.RoleEnum;
import com.offer.oj.service.QuestionService;
import com.offer.oj.service.UserService;
import com.offer.oj.util.LoginCacheUtil;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/v1")
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;
    @GetMapping("/question/add")
    @ResponseBody
    public Result addQuestion(@CookieValue(required = false, value = "TOKEN") Cookie cookie, QuestionDTO questionDTO) throws IOException {
        Result result = new Result();
        String message = "";
        if(null!=cookie){
            Integer userId= LoginCacheUtil.loginUser.get(cookie.getValue());
            String username;
            if((username = (userService.verifyRole(userId,"Teacher"))) !=null){
                questionDTO.setUsername(username);
                result =  questionService.addQuestion(questionDTO);
            }
            else {
                message = "Permission denied!";
                result.setSuccess(false);
                result.setMessage(message);
            }
        }
        else{
            message = "Cannot Get User Info!";
            result.setSuccess(false);
            result.setMessage(message);
        }
        return result;
    }


    @DeleteMapping("/question/delete")
    public Result deleteQuestion(@CookieValue("TOKEN") Cookie cookie, Integer questionId) {
        String message = "";
        Result result = new Result();
        if (null != cookie) {
            Integer userId = LoginCacheUtil.loginUser.get(cookie.getValue());
            String username;
            QuestionDTO questionDTO = new QuestionDTO();
            if ((username = (userService.verifyRole(userId, RoleEnum.TEACHER.getValue()))) != null) {
                questionDTO.setUsername(username);
                questionDTO.setId(questionId);
                result = questionService.deleteQuestion(questionDTO);
            } else {
                message = "Permission denied!";
                result.setSuccess(false);
                result.setMessage(message);
            }
        } else {
            message = "Cannot Get User Info!";
            result.setSuccess(false);
            result.setMessage(message);
        }
        return result;
    }

    @PutMapping("/question/modify")
    public Result modifyQuestion(@CookieValue("TOKEN") Cookie cookie, @Validated @RequestBody QuestionDTO questionDTO, BindingResult bindingResult) {
        String message = "";
        Result result = new Result<>();
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(s -> log.info(s.getDefaultMessage()));
            message = "Question Info incomplete.";
            result.setSuccess(false);
            result.setMessage(message);
        } else if (null != cookie) {
            Integer userId = LoginCacheUtil.loginUser.get(cookie.getValue());
            String username;
            if ((username = (userService.verifyRole(userId, RoleEnum.TEACHER.getValue()))) != null) {
                questionDTO.setUsername(username);
                result = questionService.modifyQuestion(questionDTO);
            } else {
                message = "Permission denied!";
                result.setSuccess(false);
                result.setMessage(message);
            }
        } else {
            message = "Cannot Get User Info!";
            result.setSuccess(false);
            result.setMessage(message);
        }
        return result;
    }

}
