package com.lprakapovich.authorizationservice.exception;

import org.springframework.http.HttpStatus;

public class DisabledUserException extends ApplicationException {

    private static final  String DEFAULT_DISABLED_USER_MESSAGE = "User is disabled";

    public DisabledUserException() {
        super(DEFAULT_DISABLED_USER_MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
