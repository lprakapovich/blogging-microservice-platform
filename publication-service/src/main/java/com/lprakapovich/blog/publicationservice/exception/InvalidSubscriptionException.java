package com.lprakapovich.blog.publicationservice.exception;

import org.springframework.http.HttpStatus;

public class InvalidSubscriptionException extends ApplicationException {

    private static final  String INVALID_SUBSCRIPTION_MESSAGE = "Subscription is invalid";

    public InvalidSubscriptionException() {
        super(INVALID_SUBSCRIPTION_MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
