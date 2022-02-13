package com.lprakapovich.authorizationservice.domain;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class UserData {

    @NotBlank(message = "Password cannot be blank")
    private String password;
    @NotBlank(message = "Username cannot be blank")
    private String username;
}
