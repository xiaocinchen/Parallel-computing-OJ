package com.offer.oj.controller;

import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.QuestionDTO;
import com.offer.oj.domain.dto.VariableQuestionDTO;
import com.offer.oj.service.QuestionService;
import com.offer.oj.service.UserService;
import com.offer.oj.util.LoginCacheUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
    public Result deleteQuestion(HttpServletRequest request, Integer questionId) {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setUsername((String) request.getAttribute("username"));
        questionDTO.setId(questionId);
        return questionService.deleteQuestion(questionDTO);
    }

    @PutMapping("/question/modify")
    public Result modifyQuestion(HttpServletRequest request, @Validated @RequestBody VariableQuestionDTO variableQuestionDTO, BindingResult bindingResult) {
        String message = "";
        Result result = new Result<>();
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(s -> log.info(s.getDefaultMessage()));
            message = "Question Info incomplete.";
            result.setSimpleResult(false, message);
        } else {
            variableQuestionDTO.setModifier((String) request.getAttribute("username"));
            result = questionService.modifyQuestion(variableQuestionDTO);
        }
        return result;
    }

}
