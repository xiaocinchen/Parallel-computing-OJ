package com.offer.oj.domain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NonNull;
import org.springframework.validation.annotation.Validated;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@Validated
public class QuestionDTO implements Serializable {

    private String username;

    @DecimalMin(value = "0",message = "The question id should be positive! ")
    private Integer id;

    private Date createTime;

    private Date modifyTime;

    @Size(max = 25,min = 2,message = "The title should be between 2 and 25 characters! ")
    @NotNull
    private String title;

    @Size(max = 1000,min = 20,message = "The description should be between 20 and 1000 characters! ")
    @NotNull
    private String description;

    private String modifier;

    private boolean status;

    private String pictureUrl;

    private String category;

    @Serial
    private static final long serialVersionUID = 1L;
}
