package com.offer.oj.dao;

import com.offer.oj.domain.dto.CodeResultDTO;
import com.offer.oj.domain.dto.CodeSimpleResultDTO;
import com.offer.oj.domain.dto.SelectCodeDTO;
import com.offer.oj.domain.query.CodeInnerQuery;
import com.offer.oj.domain.query.CodeResultListQuery;

import java.util.List;

public interface CodeMapper {
    boolean submitCode(CodeInnerQuery codeInnerQuery);

    boolean updateCodeByFileName(CodeInnerQuery codeInnerQuery);

    SelectCodeDTO queryCodeByName(String fileName);

    boolean selectAndDeleteCodeByName(String fileName);

    List<CodeSimpleResultDTO> queryCodeResultListByAuthorId(CodeResultListQuery codeResultListQuery);
}
