package com.offer.oj.domain.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class LoginDTO implements Serializable {

    private String username;
    private String password;

    @Serial
    private static final long serialVersionUID = 1L;
}
