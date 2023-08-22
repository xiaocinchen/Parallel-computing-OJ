package com.offer.oj.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionSynDTO implements Serializable {
    private Integer id;
    private String cacheKey;
    @Serial
    private static final long serialVersionUID = 1L;
}
