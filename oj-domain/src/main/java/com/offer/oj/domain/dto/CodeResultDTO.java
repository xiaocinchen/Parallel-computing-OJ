package com.offer.oj.domain.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class CodeResultDTO implements Serializable {

    private Integer code;

    private String result;

    private String status;

    private Integer executionTime;

    private Integer executionMemory;

    private String fileName;

    private String message;

    private Integer testNumber;

    private Integer acNumber;

    private final String filePattern = "/.+?(?=[\\p{Punct}&&[^./_]])";

    @Serial
    private static final long serialVersionUID = 1L;

    public void setMessage(String message) {
        this.message = message.replaceAll(filePattern,"");
    }
}
