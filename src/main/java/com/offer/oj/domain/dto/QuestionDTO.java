package com.offer.oj.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class QuestionDTO implements Serializable {

    private String username;

    private Integer questionId;

    private String title;

    private String description;

    private String modifier;

    private boolean status;

    private String pictureUrl;

    private String category;
}
