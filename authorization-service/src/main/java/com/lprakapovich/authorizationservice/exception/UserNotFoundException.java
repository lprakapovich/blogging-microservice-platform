package com.lprakapovich.authorizationservice.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApplicationException {

    private static final  String DEFAULT_USER_NOT_FOUND_MESSAGE = "User not found";

    public UserNotFoundException() {
        super(DEFAULT_USER_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND);
    }
}
