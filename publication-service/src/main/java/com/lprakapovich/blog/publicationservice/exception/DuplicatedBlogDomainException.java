package com.lprakapovich.blog.publicationservice.exception;

import org.springframework.http.HttpStatus;

public class DuplicatedBlogDomainException extends ApplicationException {
    private static final String DUPLICATED_DOMAIN_MESSAGE = "Blog with such a domain alreayd exists";

    public DuplicatedBlogDomainException() {
        super(DUPLICATED_DOMAIN_MESSAGE, HttpStatus.CONFLICT);
    }
}
