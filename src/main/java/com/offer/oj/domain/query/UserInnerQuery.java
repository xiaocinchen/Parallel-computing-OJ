package com.offer.oj.domain.query;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInnerQuery extends BaseInnerQuery implements Serializable {

    private Integer id;

    private String username;

    private String firstName;

    private String lastName;

    private String gender;

    private String password;

    private String role;

    private String email;

}
