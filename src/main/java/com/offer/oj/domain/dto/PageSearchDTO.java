package com.offer.oj.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.io.Serial;
import java.io.Serializable;

@Data
public class PageSearchDTO implements Serializable {

    @NotNull
    private String title;

    @NotNull
    private Integer pageIndex;

    @NotNull
    private Integer pageSize;

    @Serial
    private static final long serialVersionUID = 1L;
}
