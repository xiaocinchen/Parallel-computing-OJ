package com.offer.oj.domain.enums;

/**
 * @Description: 代码运行结果Enum
 * @author xiaocinchen
 * @date 2023/7/17 11:54
 */
public enum CodeResultEnum {

    ACCEPT("ACCEPT", 0),
    WRONG_ANSWER("Wrong Answer", 1),
    RUNTIME_ERROR("Runtime Error", 2),
    TIME_LIMIT_EXCEEDED("Time Limit Exceeded", 3, 3000),

    MEMORY_LIMIT_EXCEEDED("Memory Limit Exceeded", 4),

    OUTPUT_LIMIT_EXCEEDED("Output Limit Exceeded", 5),

    COMPILE_ERROR("Compile Error", 6),
    ;

    private final String Result;
    private final Integer code;

    private Integer limit = 3000;

    private CodeResultEnum(String Result, Integer code) {
        this.Result = Result;
        this.code = code;
    }

    private CodeResultEnum(String Result, Integer code, Integer limit) {
        this.Result = Result;
        this.code = code;
        this.limit = limit;
    }

    public String getResult() {
        return Result;
    }

    public Integer getCode() {
        return code;
    }

    public Integer getLimit(){
        return limit;
    }
}
