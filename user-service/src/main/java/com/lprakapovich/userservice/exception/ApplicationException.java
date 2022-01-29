package com.lprakapovich.userservice.exception;

import com.lprakapovich.userservice.exception.error.ErrorEnvelope;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApplicationException extends RuntimeException {

    protected final ErrorEnvelope errorEnvelope;

    public ApplicationException(String message, HttpStatus status) {
        super(message);
        errorEnvelope = new ErrorEnvelope(message, status, status.value());
    }
}
