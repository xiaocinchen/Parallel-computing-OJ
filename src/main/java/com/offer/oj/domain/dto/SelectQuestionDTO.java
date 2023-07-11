package com.offer.oj.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SelectQuestionDTO implements Serializable {

    private String title;

    private String description;

    private String pictureUrl;

    private String category;
}
