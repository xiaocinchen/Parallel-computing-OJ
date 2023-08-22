package com.offer.oj.controller;

import com.offer.oj.domain.Result;
import com.offer.oj.domain.dto.QuestionGroupDTO;
import com.offer.oj.domain.dto.UserIdentityDTO;
import com.offer.oj.platform.service.QuestionGroupService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/v1")
public class QuestionGroupController {
    @Autowired
    private QuestionGroupService questionGroupService;

    @PostMapping("/group/add")
    @ResponseBody
    public Result addGroup(HttpServletRequest request, @Validated @RequestBody QuestionGroupDTO questionGroup, BindingResult bindingResult){
        Result result = new Result();
        String message = "";
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(s -> log.info(s.getDefaultMessage()));
            message = "Question Info incomplete.";
            result.setSimpleResult(false, message);
        } else {
            questionGroup.setModifier(((UserIdentityDTO) request.getAttribute("UserIdentityDTO")).getUsername());
            result = questionGroupService.addQuestionGroup(questionGroup);
        }
        return result;
    }
    @PostMapping("/group/delete")
    @ResponseBody
    public Result deleteGroup(HttpServletRequest request, @Validated @RequestBody Integer groupId, BindingResult bindingResult){
        QuestionGroupDTO questionGroup = new QuestionGroupDTO();
        questionGroup.setModifier(((UserIdentityDTO) request.getAttribute("UserIdentityDTO")).getUsername());
        questionGroup.setId(groupId);
        return questionGroupService.deleteQuestionGroup(questionGroup);
    }

    @PostMapping("/group/search")
    @ResponseBody
    public Result searchGroup(HttpServletRequest request, @Validated @RequestBody String groupName){
        return questionGroupService.searchQuestionGroup(groupName);
    }

    @PostMapping("/group/modify")
    @ResponseBody
    public Result modifyGroup(HttpServletRequest request, @Validated @RequestBody QuestionGroupDTO questionGroup, BindingResult bindingResult){
        String message = "";
        Result result = new Result<>();
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(s -> log.info(s.getDefaultMessage()));
            message = "Question Info incomplete.";
            result.setSimpleResult(false, message);
        } else {
            questionGroup.setModifier(((UserIdentityDTO) request.getAttribute("UserIdentityDTO")).getUsername());
            result = questionGroupService.modifyQuestionGroup(questionGroup);
        }
        return result;
    }

}
