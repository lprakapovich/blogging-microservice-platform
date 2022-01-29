package com.lprakapovich.authorizationservice.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

public class JwtException extends ApplicationException {

    public JwtException(Cause cause) {
        super(cause.message, HttpStatus.UNAUTHORIZED);
    }

    @AllArgsConstructor
    public enum Cause {
        EXPIRED("Expired JWT"),
        INVALID("Invalid JWT");
        String message;
    }
}
