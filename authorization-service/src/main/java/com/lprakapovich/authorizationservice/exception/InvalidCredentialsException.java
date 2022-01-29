package com.lprakapovich.authorizationservice.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends ApplicationException {
    private static final  String DEFAULT_INVALID_CREDENTIALS_MESSAGE = "Credentials are not valid";

    public InvalidCredentialsException() {
        super(DEFAULT_INVALID_CREDENTIALS_MESSAGE, HttpStatus.UNAUTHORIZED);
    }
}
