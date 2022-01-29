package com.lprakapovich.blog.publicationservice.exception;

import org.springframework.http.HttpStatus;

public class FeignResponseMappingException extends ApplicationException {

    private static final  String DEFAULT_FEIGN_FALLBACK_MESSAGE = "Could not map the response from feign client to ErrorEnvelope.class";

    public FeignResponseMappingException() {
        super(DEFAULT_FEIGN_FALLBACK_MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
