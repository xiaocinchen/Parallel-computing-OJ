package com.offer.oj.domain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class VariableQuestionDTO implements Serializable {

    private final static String INFO_INCOMPLETE_LOG = "Incomplete question information";

    //ID cant be changed, just for search.
    @DecimalMin(value = "0",message = "The question id should be positive! ")
    @NotNull(message = INFO_INCOMPLETE_LOG)
    private Integer id;

    @Size(max = 25,min = 2,message = "The title should be between 2 and 25 characters! ")
    @NotNull(message = INFO_INCOMPLETE_LOG)
    private String title;

//    @Size(max = 1000,min = 20,message = "The description should be between 20 and 1000 characters! ")
    @NotNull(message = INFO_INCOMPLETE_LOG)
    private QuestionDescriptionDTO description;

    private String modifier;

    private boolean status;

    private String pictureUrl;

    private String category;

    private Integer timeLimit;

    private Integer memoryLimit;

    @Serial
    private static final long serialVersionUID = 1L;
}
