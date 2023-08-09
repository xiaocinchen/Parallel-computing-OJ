package com.offer.oj.domain.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class CompareFileDTO implements Serializable {

    private boolean isSame;

    private Integer differentLineNumber;

    private String differentLine;
    @Serial
    private static final long serialVersionUID = 1L;
}
