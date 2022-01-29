package com.lprakapovich.blog.publicationservice.exception.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorEnvelope {

    private String message;
    private HttpStatus responseStatus;
    private int responseStatusCode;

    public ErrorEnvelope(String message, int statusCode) {
        this.message = message;
        this.responseStatusCode = statusCode;
        this.responseStatus = HttpStatus.resolve(statusCode);
    }

    public ErrorEnvelope(String message, HttpStatus status) {
        this.message = message;
        this.responseStatus = status;
        this.responseStatusCode = status.value();
    }

    public ErrorEnvelope(int statusCode) {
        this.responseStatusCode = statusCode;
    }
}
