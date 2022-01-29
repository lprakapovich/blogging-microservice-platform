package com.lprakapovich.userservice.exception.error;

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
}
