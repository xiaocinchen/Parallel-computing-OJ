package com.offer.oj.domain.enums;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum GenderEnum {
    MALE("male"),
    FEMALE("female"),
    SECRET("secret"),
    ;

    private final String gender;

    GenderEnum(String gender){
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    @Override
    public String toString() {
        return gender;
    }

    public static Set<String> getGenderSet(){
        return Arrays.stream(GenderEnum.values()).map(GenderEnum::getGender).collect(Collectors.toSet());
    }
}
