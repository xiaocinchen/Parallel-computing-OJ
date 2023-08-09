package com.offer.oj.controller;

import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.CodeStageDTO;
import com.offer.oj.domain.dto.SubmitCodeDTO;
import com.offer.oj.domain.dto.UserIdentityDTO;
import com.offer.oj.domain.dto.CodeResultListDTO;
import com.offer.oj.service.CodeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/v1/code")
public class CodeController {

    @Autowired
    private CodeService codeService;

    @PostMapping("/submit")
    public Result codeSubmit(@Validated @RequestBody SubmitCodeDTO submitCodeDTO, HttpServletRequest request) {
        submitCodeDTO.setAuthorId(((UserIdentityDTO)request.getAttribute("UserIdentityDTO")).getUserId());
        submitCodeDTO.setIsResult(false);
        return codeService.submitCode(submitCodeDTO);
    }

    @PostMapping("/propose")
    public Result codePropose(@Validated @RequestBody SubmitCodeDTO submitCodeDTO, HttpServletRequest request) {
        submitCodeDTO.setAuthorId(((UserIdentityDTO)request.getAttribute("UserIdentityDTO")).getUserId());
        submitCodeDTO.setIsResult(true);
        return codeService.submitCode(submitCodeDTO);
    }

    @PostMapping("/stage/submit")
    public Result codeStageSubmit(@Validated @RequestBody CodeStageDTO codeStageDTO, HttpServletRequest request){
        codeStageDTO.setAuthorId(((UserIdentityDTO)request.getAttribute("UserIdentityDTO")).getUserId());
        return codeService.stageCodeSubmit(codeStageDTO);
    }

    @GetMapping("/stage/get")
    public Result<CodeStageDTO> codeStageGet(@Validated @RequestParam CodeStageDTO codeStageDTO, HttpServletRequest request){
        codeStageDTO.setAuthorId(((UserIdentityDTO)request.getAttribute("UserIdentityDTO")).getUserId());
        return codeService.stageCodeGet(codeStageDTO);
    }

    @GetMapping("/code/result")
    public Result<CodeResultListDTO> codeResult(){
        return null;
    }
}
