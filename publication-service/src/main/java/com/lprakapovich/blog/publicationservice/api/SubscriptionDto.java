package com.lprakapovich.blog.publicationservice.api;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
class SubscriptionDto {

    @NotBlank(message = "Blog ID cannot be blank")
    private String blogId;

    @NotBlank(message = "Subscriber ID cannot be blank")
    private String subscriberId;
}
