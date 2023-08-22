package com.offer.oj.domain.enums;

public enum CodeStatusEnum {
    PENDING("pending"),
    SUCCESS("success"),
    FAIL("fail"),
    ;

    private final String status;

    CodeStatusEnum(String status){
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
