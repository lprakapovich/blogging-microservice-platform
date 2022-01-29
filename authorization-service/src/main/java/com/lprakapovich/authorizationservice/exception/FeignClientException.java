package com.lprakapovich.authorizationservice.exception;

import org.springframework.http.HttpStatus;

public class FeignClientException extends ApplicationException {

    public FeignClientException(String message, HttpStatus status) {
        super(message, status);
    }
}
