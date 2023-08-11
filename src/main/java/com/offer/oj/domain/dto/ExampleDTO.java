package com.offer.oj.domain.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ExampleDTO implements Serializable {

    private String input;

    private String output;

    private String exampleDescription;

    @Serial
    private static final long serialVersionUID = 1L;
}
