package com.offer.oj.domain.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
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
