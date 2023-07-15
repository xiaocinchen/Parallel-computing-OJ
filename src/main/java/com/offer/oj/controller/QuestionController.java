package com.offer.oj.controller;

import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.QuestionDTO;
import com.offer.oj.domain.dto.VariableQuestionDTO;
import com.offer.oj.domain.enums.RoleEnum;
import com.offer.oj.service.QuestionService;
import com.offer.oj.service.UserService;
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

    @PostMapping("/question/add")
    @ResponseBody
    public Result addQuestion(HttpServletRequest request, @Validated @RequestBody VariableQuestionDTO variableQuestionDTO, BindingResult bindingResult) throws IOException {
        Result result = new Result();
        String message = "";
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(s -> log.info(s.getDefaultMessage()));
            message = "Question Info incomplete.";
            result.setSimpleResult(false, message);
        } else {
            variableQuestionDTO.setModifier((String) request.getAttribute("username"));
            result = questionService.addQuestion(variableQuestionDTO);
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
