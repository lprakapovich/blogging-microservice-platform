package com.lprakapovich.blog.publicationservice.exception;

import org.springframework.http.HttpStatus;

public class BlogIdMismatchException extends ApplicationException {

    private static final String BLOG_ID_MISMATCH_MESSAGE = "Blog id from the token doesn't match the one from the path";

    public BlogIdMismatchException() {
        super(BLOG_ID_MISMATCH_MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
