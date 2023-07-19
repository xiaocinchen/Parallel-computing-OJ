package com.offer.oj.domain.enums;

public enum MQQueueEnum {
    EMAIL_QUEUE("email_queue"),
    CODE_JUDGE_QUEUE("code_judge_queue"),
    CODE_RESULT_QUEUE("code_result_queue"),
    ;

    private final String value;
    private MQQueueEnum(String value){
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
