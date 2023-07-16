package com.offer.oj.domain.enums;

public enum CodeStatusEnum {

    ACCEPT("ACCEPT", 0),
    WRONG_ANSWER("Wrong Answer", 1),
    RUNTIME_ERROR("Runtime Error", 2),
    TIME_LIMIT_EXCEEDED("Time Limit Exceeded", 3),

    MEMORY_LIMIT_EXCEEDED("Memory Limit Exceeded", 4),

    OUTPUT_LIMIT_EXCEEDED("Output Limit Exceeded", 5),

    COMPILE_ERROR("Compile Error", 6),
    ;

    private CodeStatusEnum(String type, Integer code) {
        this.type = type;
        this.code = code;
    }

    private final String type;
    private final Integer code;

    public String getType() {
        return type;
    }

    public Integer getCode() {
        return code;
    }
}
