package com.lprakapovich.blog.publicationservice.exception;

import org.springframework.http.HttpStatus;

public class FeignClientException extends ApplicationException {

    public FeignClientException(String message, HttpStatus status) {
        super(message, status);
    }

    public FeignClientException(String message, int statusCode) {
        super(message, statusCode);
    }

    public FeignClientException(int statusCode) {
        super(statusCode);
    }
}
