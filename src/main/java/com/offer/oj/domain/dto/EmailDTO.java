package com.offer.oj.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class EmailDTO implements Serializable {
    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private String role;

    private String content;

    private String subject;

    private String code;

    private boolean isHtml;
}
