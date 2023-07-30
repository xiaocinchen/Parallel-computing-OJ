package com.offer.oj.domain.dto;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

@Data
public class PageSearchDTO implements Serializable {

    String title;

    Integer pageIndex;

    Integer pageSize;
}
