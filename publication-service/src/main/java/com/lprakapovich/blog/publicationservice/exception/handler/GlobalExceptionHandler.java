package com.lprakapovich.blog.publicationservice.exception.handler;

import com.lprakapovich.blog.publicationservice.exception.ApplicationException;
import com.lprakapovich.blog.publicationservice.exception.error.ErrorEnvelope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handleDtoValidationException(MethodArgumentNotValidException e) {
        List<String> validationErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> String.join(":", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(validationErrors);
    }

    @ExceptionHandler(value = ApplicationException.class)
    public ResponseEntity<ErrorEnvelope> handleApplicationException(ApplicationException e) {
        return buildDefaultResponseEntity(e);
    }

    private ResponseEntity<ErrorEnvelope> buildDefaultResponseEntity(ApplicationException e) {
        e.printStackTrace();
        return new ResponseEntity<>(e.getErrorEnvelope(), e.getErrorEnvelope().getResponseStatus());
    }
}
