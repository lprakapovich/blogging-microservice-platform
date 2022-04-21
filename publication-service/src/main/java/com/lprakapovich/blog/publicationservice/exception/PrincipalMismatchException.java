package com.lprakapovich.blog.publicationservice.exception;

import org.springframework.http.HttpStatus;

public class PrincipalMismatchException extends ApplicationException {

    private static final String PRINCIPAL_MISMATCH_MESSAGE = "Principal from the token doesn't match the one from the path";

    public PrincipalMismatchException() {
        super(PRINCIPAL_MISMATCH_MESSAGE, HttpStatus.FORBIDDEN);
    }
}
