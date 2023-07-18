package com.offer.oj.domain.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class SelectCodeDTO implements Serializable {
    private Integer id;

    private String fileName;

    private String type;

    private Integer authorId;

    private String status;

    private String result;

    private Date createTime;

    private Integer executionTime;

    @Serial
    private static final long serialVersionUID = 1L;
}
