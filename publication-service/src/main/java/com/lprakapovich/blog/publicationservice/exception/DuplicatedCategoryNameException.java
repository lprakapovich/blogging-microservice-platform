package com.lprakapovich.blog.publicationservice.exception;

import org.springframework.http.HttpStatus;

public class DuplicatedCategoryNameException extends ApplicationException {

    private static final String DUPLICATED_CATEGORY_NAME_MESSAGE = "Category with such a name already exists";

    public DuplicatedCategoryNameException() {
        super(DUPLICATED_CATEGORY_NAME_MESSAGE, HttpStatus.CONFLICT);
    }
}
