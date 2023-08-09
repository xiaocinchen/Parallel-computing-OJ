package com.offer.oj.domain.dto;

import java.io.Serializable;

public class CodeResultListDTO implements Serializable {

    private Integer id;
    private String type;
    private String result;

    private String status;

    private Integer executionTime;

    private Integer executionMemory;
}
