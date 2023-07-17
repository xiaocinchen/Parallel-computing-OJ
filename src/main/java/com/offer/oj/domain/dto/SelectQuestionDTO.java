package com.offer.oj.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class SelectQuestionDTO implements Serializable {
    private final static String INFO_INCOMPLETE_LOG = "Query Cannot Be Null!";

    @NotNull(message = INFO_INCOMPLETE_LOG)
    private String title;

    private String category;
}
