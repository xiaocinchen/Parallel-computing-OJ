package com.offer.oj.domain.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@NotNull
public class UserDTO implements Serializable {

    @NotNull
    @Size(min = 6, max = 20, message = "The username should be between 6 and 20 characters!")
    private String username;

    @NotNull
    @Email(message = "Email format error!")
    private String email;

    @NotNull
    @Size(max = 12, message = "FirstName cannot exceed 12 characters!")
    private String firstName;

    @NotNull
    @Size(max = 12, message = "LastName cannot exceed 12 characters!")
    private String lastName;

    @NotNull
    private String gender;

    @NotNull
    @Size(min = 8, max = 20, message = "Password should be composed of 8 to 20 characters of numbers or English!")
    @Pattern(regexp = "[a-zA-Z0-9]+", message = "Password should be numbers or English!")
    private String password;

    @Null
    private String role;

    @Serial
    private static final long serialVersionUID = 1L;

}
