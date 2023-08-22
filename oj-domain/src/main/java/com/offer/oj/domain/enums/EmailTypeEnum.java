package com.offer.oj.domain.enums;

public enum EmailTypeEnum {
    REGISTER("REGISTER"), CHANGE_PASSWORD("CHANGE_PASSWORD");
    private final String value;

    private EmailTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
