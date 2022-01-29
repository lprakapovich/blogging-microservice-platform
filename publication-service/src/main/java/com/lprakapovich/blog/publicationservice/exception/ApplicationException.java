package com.lprakapovich.blog.publicationservice.exception;

import com.lprakapovich.blog.publicationservice.exception.error.ErrorEnvelope;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApplicationException extends RuntimeException {

    protected final ErrorEnvelope errorEnvelope;

    public ApplicationException(String message, HttpStatus status) {
        super(message);
        errorEnvelope = new ErrorEnvelope(message, status);
    }

    public ApplicationException(String message, int statusCode) {
        errorEnvelope = new ErrorEnvelope(message, statusCode);
    }

    public ApplicationException(int statusCode) {
        errorEnvelope = new ErrorEnvelope(statusCode);
    }
}
