package com.offer.oj.domain.enums;

public enum CookieEnum {
    TOKEN("TOKEN"),
    TEMP_LICENCE("TEMP_LICENCE"),
    KAPTCHA("kaptcha"),
    ;

    private final String name;

    CookieEnum(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "CookieEnum{" +
                "name='" + name + '\'' +
                '}';
    }
}
