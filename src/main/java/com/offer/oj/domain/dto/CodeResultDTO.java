package com.offer.oj.domain.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class CodeResultDTO implements Serializable {

    private Integer code;

    private String result;

    private String status;

    private Integer time;

    private Integer memory;

    private String fileName;

    private String error;

    @Serial
    private static final long serialVersionUID = 1L;
}
