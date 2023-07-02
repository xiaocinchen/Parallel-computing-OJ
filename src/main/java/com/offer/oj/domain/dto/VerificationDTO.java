package com.offer.oj.domain.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class VerificationDTO implements Serializable {
    private String username;

    private String code;

    private String type;
}
