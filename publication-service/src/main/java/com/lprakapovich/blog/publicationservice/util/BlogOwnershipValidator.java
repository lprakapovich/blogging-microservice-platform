package com.lprakapovich.blog.publicationservice.util;

import com.lprakapovich.blog.publicationservice.model.Blog;
import com.lprakapovich.blog.publicationservice.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BlogOwnershipValidator {

    private final BlogService blogService;

    public void validate(Blog.BlogId id) {
        PrincipalValidator.validate(id.getUsername());
        blogService.checkExistence(id);
    }
}
