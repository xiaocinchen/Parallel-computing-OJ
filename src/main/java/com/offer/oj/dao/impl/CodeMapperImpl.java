package com.offer.oj.dao.impl;

import com.offer.oj.dao.CodeMapper;
import com.offer.oj.dao.mapper.OjCodeMapper;
import com.offer.oj.domain.OjCode;
import com.offer.oj.domain.dto.SelectCodeDTO;
import com.offer.oj.domain.query.CodeInnerQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class CodeMapperImpl implements CodeMapper {

    @Autowired
    private OjCodeMapper ojCodeMapper;

    @Override
    public boolean submitCode(CodeInnerQuery codeInnerQuery){
        OjCode ojCode = new OjCode();
        BeanUtils.copyProperties(codeInnerQuery, ojCode);
        try {
            ojCodeMapper.insertSelective(ojCode);
            return true;
        } catch (Exception e){
            log.warn("Submit code failed.");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateCodeByFileName(CodeInnerQuery codeInnerQuery) {
        OjCode ojCode = new OjCode();
        ojCode.setResult(codeInnerQuery.getResult());
        ojCode.setFileName(codeInnerQuery.getFileName());
        try{
            ojCodeMapper.updateByFileName(ojCode);
            return true;
        } catch (Exception e){
            log.warn("Update code status wrong.");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public SelectCodeDTO queryCodeByName(String fileName){
        return null;
    }

}
