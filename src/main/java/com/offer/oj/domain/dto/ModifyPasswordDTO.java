package com.offer.oj.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ModifyPasswordDTO implements Serializable {
    @NotNull
    private String username;
    @NotNull
    private String password;
    @NotNull
    private String kaptchaCode;
    @Serial
    private static final long serialVersionUID = 1L;
}
