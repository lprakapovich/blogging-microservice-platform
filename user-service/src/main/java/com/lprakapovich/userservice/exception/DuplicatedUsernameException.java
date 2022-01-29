package com.lprakapovich.userservice.exception;

import org.springframework.http.HttpStatus;

public class DuplicatedUsernameException extends ApplicationException{

    private static final String DEFAULT_DEDUPLICATION_MESSAGE = "Username is duplicated";

    public DuplicatedUsernameException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public DuplicatedUsernameException() {
        this(DEFAULT_DEDUPLICATION_MESSAGE);
    }
}
