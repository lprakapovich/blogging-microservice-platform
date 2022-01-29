package com.lprakapovich.blog.publicationservice.exception;

import org.springframework.http.HttpStatus;

public class BlogNotFoundException extends ApplicationException {

    private static final String DEFAULT_BLOG_NOT_FOUND_MESSAGE = "Blog not found";

    public BlogNotFoundException() {
        super(DEFAULT_BLOG_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND);
    }
}
