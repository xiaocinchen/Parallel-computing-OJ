package com.offer.oj.domain.enums;

public enum MQExchangeEnum {
    EMAIL_EXCHANGE("email_exchange"),
    CODE_EXCHANGE("code_exchange"),
    ;

    private final String value;

    MQExchangeEnum(String value){
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
