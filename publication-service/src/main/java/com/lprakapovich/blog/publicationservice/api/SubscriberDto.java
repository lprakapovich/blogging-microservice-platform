package com.lprakapovich.blog.publicationservice.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriberDto {

    @NotBlank(message = "Subscriber blog id cannot be blank")
    private String subscriberBlogId;
}
