package com.offer.oj.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserIdentityDTO {

    private Integer userId;

    private String username;

    private String role;
}
