package com.offer.oj.service;


import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.CodeStageDTO;
import com.offer.oj.domain.dto.SubmitCodeDTO;

public interface CodeService {

    Result submitCode(SubmitCodeDTO submitCodeDTO);

    Result stageCodeSubmit(CodeStageDTO codeStageDTO);

    Result<CodeStageDTO> stageCodeGet(CodeStageDTO codeStageDTO);
}
