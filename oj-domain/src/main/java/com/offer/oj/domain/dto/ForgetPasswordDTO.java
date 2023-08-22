package com.offer.oj.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
@Data
public class ForgetPasswordDTO implements Serializable {

    @NotNull
    private String username;

    @NotNull
    private String email;

    @Serial
    private static final long serialVersionUID = 1L;
}
