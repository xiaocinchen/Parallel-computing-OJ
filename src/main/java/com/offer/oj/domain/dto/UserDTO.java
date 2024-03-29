package com.offer.oj.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserDTO implements Serializable {

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private String gender;

    private String password;

    private String role;

}
