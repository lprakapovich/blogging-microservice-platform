package com.lprakapovich.userservice.api;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserDto {

    @NotBlank(message = "Username cannot be empty")
    private String username;

    @NotBlank(message = "Password cannot be empty")
    private String password;

    @NotBlank(message = "First name cannot be empty")
    private String firstName;

    @NotBlank(message = "Last name cannot be empty")
    private String lastName;
}
