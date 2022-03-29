package com.lprakapovich.blog.publicationservice.api.dto;

import com.lprakapovich.blog.publicationservice.model.Blog.BlogId;
import lombok.Data;

@Data
public class BlogDto {

    private BlogId id;
    private String displayName;
    private String description;
}

