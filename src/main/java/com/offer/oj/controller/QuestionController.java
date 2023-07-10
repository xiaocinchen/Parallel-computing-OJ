package com.offer.oj.controller;

import com.offer.oj.dao.Result;
import com.offer.oj.dao.UserMapper;
import com.offer.oj.dao.mapper.OjUserMapper;
import com.offer.oj.domain.OjUser;
import com.offer.oj.domain.dto.QuestionDTO;
import com.offer.oj.service.QuestionService;
import com.offer.oj.util.LoginCacheUtil;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/v1")
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private OjUserMapper ojUserMapper;

    @GetMapping("/add-question")
    @ResponseBody
    public Result addQuestion(@CookieValue(required = false, value = "TOKEN") Cookie cookie, QuestionDTO questionDTO) throws IOException {
        Result<String> result = new Result<>();
        String message = "";
        if(null!=cookie){
            Integer userId= LoginCacheUtil.loginUser.get(cookie.getValue());
            OjUser user=ojUserMapper.selectByPrimaryKey(userId);
            if(user.getRole().equals("teacher")){
                result =  questionService.addQuestion(user, questionDTO);
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
}
