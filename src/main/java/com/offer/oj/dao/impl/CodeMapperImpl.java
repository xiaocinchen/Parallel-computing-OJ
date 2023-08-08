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
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class CodeMapperImpl implements CodeMapper {

    @Autowired
    private OjCodeMapper ojCodeMapper;

    @Override
    public boolean submitCode(CodeInnerQuery codeInnerQuery) {
        OjCode ojCode = new OjCode();
        BeanUtils.copyProperties(codeInnerQuery, ojCode);
        try {
            ojCodeMapper.insertSelective(ojCode);
            return true;
        } catch (Exception e) {
            log.warn("Submit code failed.");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateCodeByFileName(CodeInnerQuery codeInnerQuery) {
        OjCode ojCode = new OjCode();
        BeanUtils.copyProperties(codeInnerQuery, ojCode);
        try {
            ojCodeMapper.updateByFileName(ojCode);
            return true;
        } catch (Exception e) {
            log.warn("Update code status wrong.");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public SelectCodeDTO queryCodeByName(String fileName) {
        CodeInnerQuery codeInnerQuery = new CodeInnerQuery();
        codeInnerQuery.setFileName(fileName);
        try {
            SelectCodeDTO selectCodeDTO = new SelectCodeDTO();
            List<OjCode> ojCodeList = ojCodeMapper.queryForList(codeInnerQuery);
            if (!CollectionUtils.isEmpty(ojCodeList) && ojCodeList.get(0) != null) {
                BeanUtils.copyProperties(ojCodeList.get(0), selectCodeDTO);
            }
            return selectCodeDTO;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean selectAndDeleteCodeByName(String fileName) {
        SelectCodeDTO selectCodeDTO = queryCodeByName(fileName);
        if (ObjectUtils.isEmpty(selectCodeDTO)) {
            return true;
        }
        try {
            ojCodeMapper.deleteByPrimaryKey(selectCodeDTO.getId());
            return true;
        } catch (Exception e) {
            log.error("Delete Code Error, fileName: {}", fileName);
            e.printStackTrace();
            return false;
        }
    }

}
