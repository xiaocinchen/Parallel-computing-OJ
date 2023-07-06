package com.offer.oj.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ForgetPasswordDTO implements Serializable {
    private String username;
    private String email;
}
