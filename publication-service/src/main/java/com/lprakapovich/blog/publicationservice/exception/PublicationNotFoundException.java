package com.lprakapovich.blog.publicationservice.exception;

import org.springframework.http.HttpStatus;

public class PublicationNotFoundException extends ApplicationException {

    private static final String DEFAULT_PUBLICATION_NOT_FOUND_MESSAGE = "Publication not found";

    public PublicationNotFoundException() {
        super(DEFAULT_PUBLICATION_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND);
    }
}
