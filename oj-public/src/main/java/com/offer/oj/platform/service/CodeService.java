package com.offer.oj.platform.service;


import com.offer.oj.domain.Result;
import com.offer.oj.domain.dto.CodeStageDTO;
import com.offer.oj.domain.dto.SubmitCodeDTO;
import com.offer.oj.domain.query.CodeResultListQuery;
import com.offer.oj.domain.dto.CodeSimpleResultDTO;

import java.util.List;

public interface CodeService {

    Result submitCode(SubmitCodeDTO submitCodeDTO);

    Result submitStageCode(CodeStageDTO codeStageDTO);

    Result<CodeStageDTO> getStageCode(CodeStageDTO codeStageDTO);

    Result<List<CodeSimpleResultDTO>> getCodeResult(CodeResultListQuery codeResultListQuery);
}
