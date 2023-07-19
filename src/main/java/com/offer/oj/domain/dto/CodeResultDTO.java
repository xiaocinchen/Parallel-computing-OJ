package com.offer.oj.domain.dto;

import lombok.Data;

@Data
public class CodeResultDTO {

    private Integer code;

    private String result;

    private String status;

    private Integer time;

    private String fileName;
}
