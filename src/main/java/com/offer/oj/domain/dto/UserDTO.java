package com.offer.oj.domain.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

@Data
@Validated
public class UserDTO implements Serializable {

    @NotNull
    @Size(max = 20, min = 6, message = "The username should be between 6 and 20 characters!")
    private String username;

    @NotNull
    @Email(message = "Email format error!")
    private String email;

    @Size(max = 12, message = "FirstName cannot exceed 12 characters!")
    private String firstName;

    @Size(max = 12, message = "LastName cannot exceed 12 characters!")
    private String lastName;

    private String gender;

    @NotNull()
    @Pattern(regexp = "[a-zA-Z0-9]+")
    @Size(max = 20, min = 8 ,message = "Password should be composed of 8 to 20 characters of numbers or English!")
    private String password;

    private String role;

}
