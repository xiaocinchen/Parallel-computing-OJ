package com.offer.oj.domain.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class SearchResultDTO implements Serializable {

    private Integer id;

    private String name;
}
