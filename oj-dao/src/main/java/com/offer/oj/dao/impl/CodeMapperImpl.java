package com.offer.oj.dao.impl;

import com.offer.oj.dao.CodeMapper;
import com.offer.oj.dao.mapper.OjCodeMapper;
import com.offer.oj.domain.pojo.OjCode;
import com.offer.oj.domain.dto.CodeSimpleResultDTO;
import com.offer.oj.domain.dto.SelectCodeDTO;
import com.offer.oj.domain.query.CodeInnerQuery;
import com.offer.oj.domain.query.CodeResultListQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
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

    @Override
    public List<CodeSimpleResultDTO> queryCodeResultListByAuthorId(CodeResultListQuery codeResultListQuery) {
        CodeInnerQuery codeInnerQuery = new CodeInnerQuery();
        BeanUtils.copyProperties(codeResultListQuery, codeInnerQuery);
        List<OjCode> ojCodeList = ojCodeMapper.queryForSimpleList(codeInnerQuery);
        if (ObjectUtils.isEmpty(ojCodeList)) {
            return Collections.emptyList();
        }
        return ojCodeList.stream().map(ojCode -> {
            CodeSimpleResultDTO codeSimpleResultDTO = new CodeSimpleResultDTO();
            BeanUtils.copyProperties(ojCode, codeSimpleResultDTO);
            return codeSimpleResultDTO;
        }).toList();
    }

}
