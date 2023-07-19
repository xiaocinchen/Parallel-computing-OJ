package com.offer.oj.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
@Data
public class VerificationDTO implements Serializable {

    @NotNull
    private String username;

    @NotNull
    private String code;

    @NotNull
    private String type;
}
