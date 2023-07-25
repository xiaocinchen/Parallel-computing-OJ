package com.offer.oj.domain.dto;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;

@Data
@Validated
public class QuestionGroupDTO {
    private  Integer group_id;
    private ArrayList<Integer> qustions;
}
