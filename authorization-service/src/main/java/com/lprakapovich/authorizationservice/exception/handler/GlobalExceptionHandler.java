package com.lprakapovich.authorizationservice.exception.handler;

import com.lprakapovich.authorizationservice.exception.*;
import com.lprakapovich.authorizationservice.exception.error.ErrorEnvelope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = InvalidCredentialsException.class)
    public ResponseEntity<ErrorEnvelope> handleInvalidCredentialsException(InvalidCredentialsException e) {
        return buildDefaultResponseEntity(e);
    }

    @ExceptionHandler(value = DisabledUserException.class)
    public ResponseEntity<ErrorEnvelope> handleDisabledUserException(DisabledUserException e) {
        return buildDefaultResponseEntity(e);
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<ErrorEnvelope> handleUserNotFoundException(UserNotFoundException e) {
        return buildDefaultResponseEntity(e);
    }

    @ExceptionHandler(value = FeignClientException.class)
    public ResponseEntity<ErrorEnvelope> handleFeignClientException(FeignClientException e) {
        return buildDefaultResponseEntity(e);
    }

    @ExceptionHandler(value = JwtException.class)
    public ResponseEntity<ErrorEnvelope> handleJwtException(JwtException e) {
        return buildDefaultResponseEntity(e);
    }

    private ResponseEntity<ErrorEnvelope> buildDefaultResponseEntity(ApplicationException e) {
        e.printStackTrace();
        return new ResponseEntity<>(e.getErrorEnvelope(), e.getErrorEnvelope().getResponseStatus());
    }
}
