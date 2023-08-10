package com.offer.oj.domain.dto;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

@Data
@Validated
public class QuestionDescriptionDTO implements Serializable {

    private String description;

    private String example;
}
