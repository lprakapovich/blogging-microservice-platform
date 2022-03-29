package com.lprakapovich.blog.publicationservice.exception;

import org.springframework.http.HttpStatus;

public class DuplicatedBlogIdException extends ApplicationException {
    private static final String DUPLICATED_BLOG_ID_MESSAGE = "Specified blog ID already exists for this username";

    public DuplicatedBlogIdException() {
        super(DUPLICATED_BLOG_ID_MESSAGE, HttpStatus.CONFLICT);
    }
}
