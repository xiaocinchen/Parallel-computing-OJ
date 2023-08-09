package com.offer.oj.domain.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class CodeInnerQuery extends BaseInnerQuery implements Serializable {

    private Integer id;

    private String fileName;

    private String type;

    private Integer authorId;

    private Integer questionId;

    private String status;

    private String result;

    private Date createTime;

    private Date modifyTime;

    private Integer executionTime;

    private Integer executionMemory;

    private String message;

    private Integer testNumber;

    private Integer acNumber;

}
