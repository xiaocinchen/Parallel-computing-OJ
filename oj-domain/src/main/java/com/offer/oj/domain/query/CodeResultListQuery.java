package com.offer.oj.domain.query;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class CodeResultListQuery implements Serializable {

    @NotNull
    private Integer authorId;
    @NotNull
    private Integer page;
    @NotNull
    private Integer pageSize;

}
