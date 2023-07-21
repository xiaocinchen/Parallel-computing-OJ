package com.offer.oj.domain.dto;

import com.offer.oj.domain.enums.KaptchaEnum;
import com.offer.oj.domain.enums.SeparatorEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class VerificationDTO implements Serializable {

    @NotNull
    private String username;

    @NotNull
    private String code;

    @NotNull
    private KaptchaEnum type;

    @Serial
    private static final long serialVersionUID = 1L;

    public String getVerificationKey() {
        return this.username + SeparatorEnum.UNDERLINE.getSeparator() + this.type.getType();
    }

}
