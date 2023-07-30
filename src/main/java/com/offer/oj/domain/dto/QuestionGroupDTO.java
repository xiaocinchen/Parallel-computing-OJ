package com.offer.oj.domain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@Validated
public class QuestionGroupDTO implements Serializable {

    @DecimalMin(value = "0",message = "The question id should be positive! ")
    private Integer id;

    @Size(max = 25,min = 2,message = "The groupName should be between 2 and 25 characters! ")
    @NotNull
    private String groupName;

    @Size(max = 1000,min = 20,message = "The description should be between 20 and 1000 characters! ")
    @NotNull
    private String description;

    private Date createTime;

    private Date modifyTime;

    private String modifier;

    private boolean status;

    @Serial
    private static final long serialVersionUID = 1L;

}
