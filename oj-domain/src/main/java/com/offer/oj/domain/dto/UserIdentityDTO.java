package com.offer.oj.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserIdentityDTO implements Serializable {

    private Integer userId;

    private String username;

    private String role;

    @Serial
    private static final long serialVersionUID = 1L;
}
