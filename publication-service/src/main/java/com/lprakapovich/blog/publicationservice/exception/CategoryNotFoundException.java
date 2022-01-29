package com.lprakapovich.blog.publicationservice.exception;

import org.springframework.http.HttpStatus;

public class CategoryNotFoundException extends ApplicationException {

    private static final String DEFAULT_CATEGORY_NOT_FOUND_MESSAGE = "Category not found";

    public CategoryNotFoundException() {
        super(DEFAULT_CATEGORY_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND);
    }
}
