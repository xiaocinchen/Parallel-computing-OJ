package com.offer.oj.domain.enums;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description: 代码语言类型Enum
 * @author xiaocinchen
 * @date 2023/7/17 11:54
 */
public enum CodeTypeEnum {
    C_PLUS_PLUS("cpp"),
    JAVA("java"),
    PYTHON("py"),
    ;

    CodeTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    private final String value;

    public static Set<String> getTypeSet(){
        return Arrays.stream(CodeTypeEnum.values()).map(CodeTypeEnum::getValue).collect(Collectors.toSet());
    }

}
