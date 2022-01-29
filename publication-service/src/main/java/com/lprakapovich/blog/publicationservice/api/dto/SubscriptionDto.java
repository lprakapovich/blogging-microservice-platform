package com.lprakapovich.blog.publicationservice.api.dto;

import javax.validation.constraints.NotBlank;

public class SubscriptionDto {

    @NotBlank(message = "Blog ID cannot be blank")
    private String blogId;

    @NotBlank(message = "Subscriber ID cannot be blank")
    private String subscriberId;
}
