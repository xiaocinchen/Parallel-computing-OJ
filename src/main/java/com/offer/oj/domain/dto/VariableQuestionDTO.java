package com.offer.oj.domain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class VariableQuestionDTO implements Serializable {

    //ID cant be changed, just for search.
    @DecimalMin(value = "0",message = "The question id should be positive! ")
    private Integer id;

    @Size(max = 25,min = 2,message = "The title should be between 2 and 25 characters! ")
    @NotNull
    private String title;

    @Size(max = 100,min = 20,message = "The description should be between 20 and 100 characters! ")
    @NotNull
    private String description;

    private String modifier;

    private boolean status;

    private String pictureUrl;

    private String category;
}
