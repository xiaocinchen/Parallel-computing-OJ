package com.offer.oj.domain.enums;

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

    CodeTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    private final String type;

}
