package com.lprakapovich.blog.publicationservice.api.dto;

import com.lprakapovich.blog.publicationservice.model.Author;
import lombok.Data;

@Data
public class BlogDto {

    private String id;
    private String description;
    private Author author;
}
