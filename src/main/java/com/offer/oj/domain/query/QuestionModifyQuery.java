package com.offer.oj.domain.query;

import lombok.Data;

import java.io.Serializable;

@Data
public class QuestionModifyQuery implements Serializable {

    private Integer id;

    private String title;

    private String modifier;

    private Boolean status;

    private String pictureUrl;

    private String category;

    private String description;

}
