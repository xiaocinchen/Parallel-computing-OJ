package com.offer.oj.domain.query;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class QuestionInnerQuery extends BaseInnerQuery implements Serializable {

    private Integer id;

    private Date createTime;

    private Date modifyTime;

    private String title;

    private String modifier;

    private Boolean status;

    private String pictureUrl;

    private String category;

    private String description;

}
