package com.offer.oj.domain.enums;

public enum CookieEnum {
    TOKEN("TOKEN"),
    TEMP_LICENCE("TEMP_LICENCE"),
    ;

    private final String value;

    CookieEnum(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "CookieEnum{" +
                "value='" + value + '\'' +
                '}';
    }
}
