package com.offer.oj.domain.enums;

public enum CacheEnum {
    USER_CACHE("userCache");
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
}
