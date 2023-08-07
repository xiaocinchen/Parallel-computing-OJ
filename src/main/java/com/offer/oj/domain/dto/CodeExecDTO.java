package com.offer.oj.domain.dto;

import com.offer.oj.domain.enums.CodeTypeEnum;
import com.offer.oj.domain.enums.SeparatorEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodeExecDTO implements Serializable {
    private String containerId;
    private CodeTypeEnum codeTypeEnum;
    private String fileName;
    private String codeFileWholePath;
    private String inputFileWholePath;
    private String outputFileWholePath;


    public String getCodeFileWholeName() {
        return codeFileWholePath + fileName;
    }

    public String getCodeFileWholeNameWithType() {
        return getCodeFileWholeName() + SeparatorEnum.DOT.getSeparator() + codeTypeEnum.getValue();
    }

}
