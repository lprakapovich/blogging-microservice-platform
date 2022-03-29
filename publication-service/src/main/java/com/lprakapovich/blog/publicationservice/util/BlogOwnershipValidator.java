package com.lprakapovich.blog.publicationservice.util;

import com.lprakapovich.blog.publicationservice.model.Blog;
import com.lprakapovich.blog.publicationservice.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BlogOwnershipValidator {

    @Autowired
    private BlogService blogService;

    public void validate(Blog.BlogId id) {
        PrincipalValidator.validate(id.getUsername());
        blogService.checkExistence(id);
    }
}
