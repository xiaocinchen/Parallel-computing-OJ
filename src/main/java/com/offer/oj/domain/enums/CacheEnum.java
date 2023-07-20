package com.offer.oj.domain.enums;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum CacheEnum {
    REGISTER_CACHE("registerCache"),
    USER_CACHE("userCache"),
    KAPTCHA_CACHE("kaptchaCache"),
    LOGIN_CACHE("loginCache"),
    SELECT_QUESTION_CACHE("selectQuestionCache"),
    STAGE_CODE_CACHE("stage_code_cache"),
    ;
    private final String value;

    private CacheEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static Set<String> getValues(){
        return Arrays.stream(CacheEnum.values()).map(CacheEnum::getValue).collect(Collectors.toSet());
    }
}
