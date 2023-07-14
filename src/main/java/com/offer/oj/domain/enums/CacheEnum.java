package com.offer.oj.domain.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum CacheEnum {
    REGISTER_CACHE("registerCache"),
    USER_CACHE("userCache"),
    KAPTCHA_CACHE("kaptchaCache"),
    LOGIN_CACHE("loginCache"),
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

    public static List<String> getValues(){
        return Arrays.stream(CacheEnum.values()).map(CacheEnum::getValue).collect(Collectors.toList());
    }
}
