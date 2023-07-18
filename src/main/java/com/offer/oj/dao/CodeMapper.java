package com.offer.oj.dao;

import com.offer.oj.domain.OjCode;
import com.offer.oj.domain.dto.SelectCodeDTO;
import com.offer.oj.domain.query.CodeInnerQuery;

public interface CodeMapper {
    boolean submitCode(CodeInnerQuery codeInnerQuery);

    boolean updateCodeByFileName(CodeInnerQuery codeInnerQuery);

    SelectCodeDTO queryCodeByName(String fileName);


}
