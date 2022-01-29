package com.lprakapovich.authorizationservice.domain;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class UserData {

    @NotBlank(message = "Password cannot be blank")
    private String password;
    @NotBlank(message = "Username cannot be blank")
    private String username;
    @NotBlank(message = "First name cannot be blank")
    private String firstName;
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;
}
