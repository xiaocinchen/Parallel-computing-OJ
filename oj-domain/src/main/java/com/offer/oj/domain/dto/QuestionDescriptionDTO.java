package com.offer.oj.domain.dto;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Validated
public class QuestionDescriptionDTO implements Serializable {

    private String description;

    private List<ExampleDTO> exampleDTOList;

    @Serial
    private static final long serialVersionUID = 1L;
}
