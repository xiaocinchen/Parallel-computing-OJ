package com.offer.oj.controller;

import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.*;
import com.offer.oj.service.QuestionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/v1")
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @PostMapping("/question/add")
    @ResponseBody
    public Result addQuestion(HttpServletRequest request, @Validated @RequestBody VariableQuestionDTO variableQuestionDTO, BindingResult bindingResult) {
        Result result = new Result();
        String message = "";
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(s -> log.info(s.getDefaultMessage()));
            message = "Question Info incomplete.";
            result.setSimpleResult(false, message);
        } else {
            variableQuestionDTO.setModifier(((UserIdentityDTO) request.getAttribute("UserIdentityDTO")).getUsername());
            result = questionService.addQuestion(variableQuestionDTO);
        }
        return result;
    }

    @DeleteMapping("/question/delete")
    public Result deleteQuestion(HttpServletRequest request, Integer questionId) {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setUsername(((UserIdentityDTO) request.getAttribute("UserIdentityDTO")).getUsername());
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
            variableQuestionDTO.setModifier(((UserIdentityDTO) request.getAttribute("UserIdentityDTO")).getUsername());
            result = questionService.modifyQuestion(variableQuestionDTO);
        }
        return result;
    }


    @PostMapping("/question/search")
    @ResponseBody
    public Result<List<SearchResultDTO>> searchQuestion(@RequestBody PageSearchDTO pageSearchDTO) {
        return questionService.queryQuestionsByTitle(pageSearchDTO);
    }
}

