package com.lprakapovich.userservice.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApplicationException {

    private static final String DEFAULT_USER_NOT_FOUND_MESSAGE = "User not found";

    public UserNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public UserNotFoundException() {
        this(DEFAULT_USER_NOT_FOUND_MESSAGE);
    }
}
