package com.lprakapovich.userservice.exception.handler;

import com.lprakapovich.userservice.exception.ApplicationException;
import com.lprakapovich.userservice.exception.DuplicatedUsernameException;
import com.lprakapovich.userservice.exception.UserNotFoundException;
import com.lprakapovich.userservice.exception.error.ErrorEnvelope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<ErrorEnvelope> handleUserNotFoundException(UserNotFoundException e) {
        return buildDefaultResponseEntity(e);
    }

    @ExceptionHandler(value = DuplicatedUsernameException.class)
    public ResponseEntity<ErrorEnvelope> handleUsernameDeduplicationException(DuplicatedUsernameException e) {
        return buildDefaultResponseEntity(e);
    }

    private ResponseEntity<ErrorEnvelope> buildDefaultResponseEntity(ApplicationException e) {
        e.printStackTrace();
        return new ResponseEntity<>(e.getErrorEnvelope(), e.getErrorEnvelope().getResponseStatus());
    }
}
