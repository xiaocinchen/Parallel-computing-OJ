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
    STAGE_CODE_CACHE("stageCodeCache"),
    QUESTION_ID_FUZZY_KEY_CACHE("questionIdFuzzyKeyCache"),
    PAGE_QUESTION_CACHE("selectPageQuestionCache"),
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
