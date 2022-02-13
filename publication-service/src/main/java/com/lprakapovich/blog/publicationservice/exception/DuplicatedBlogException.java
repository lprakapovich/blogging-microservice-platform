package com.lprakapovich.blog.publicationservice.exception;

import org.springframework.http.HttpStatus;

public class DuplicatedBlogException extends ApplicationException {
    private static final String DUPLICATED_DOMAIN_MESSAGE = "Blog with such a domain alreayd exists";

    public DuplicatedBlogException() {
        super(DUPLICATED_DOMAIN_MESSAGE, HttpStatus.CONFLICT);
    }
}
