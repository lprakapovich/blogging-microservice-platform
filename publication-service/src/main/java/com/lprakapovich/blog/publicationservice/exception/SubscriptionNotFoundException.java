package com.lprakapovich.blog.publicationservice.exception;

import org.springframework.http.HttpStatus;

public class SubscriptionNotFoundException extends ApplicationException {

    private static final String DEFAULT_SUBSCRIPTION_NOT_FOUND_MESSAGE = "Subscription not found";

    public SubscriptionNotFoundException() {
        super(DEFAULT_SUBSCRIPTION_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND);
    }
}
