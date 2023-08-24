package com.offer.oj.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class GroupQuestionDTO implements Serializable {
    private Integer id;

    private Integer groupId;

    private Integer questionId;
}
