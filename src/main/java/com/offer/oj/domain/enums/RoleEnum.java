package com.offer.oj.domain.enums;

public enum RoleEnum {
    TEACHER("teacher"), STUDENT("student");

    private final String value;

    private RoleEnum(String value) {
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
