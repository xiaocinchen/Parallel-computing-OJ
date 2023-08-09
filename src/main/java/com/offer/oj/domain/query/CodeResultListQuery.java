package com.offer.oj.domain.query;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class CodeResultListQuery implements Serializable {

    private Integer pageNumber;
    private Integer pageSize;

}
