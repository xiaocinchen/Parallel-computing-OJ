package com.offer.oj.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CodeSimpleResultDTO implements Serializable {

    private Integer id;

    private String type;

    private String result;

    private String status;

    private Date createTime;

    private Integer acNumber;

    private Integer testNumber;

    private Integer executionTime;

    private Integer executionMemory;
}
