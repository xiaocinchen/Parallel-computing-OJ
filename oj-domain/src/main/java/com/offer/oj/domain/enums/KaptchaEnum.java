package com.offer.oj.domain.enums;

public enum KaptchaEnum {
    REGISTER("register"),
    FORGET_PASSWORD("forget_password"),
    LOGIN("login"),
    ;
    private final String type;

    KaptchaEnum(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "KaptchaEnum{" +
                "type='" + type + '\'' +
                '}';
    }
}
