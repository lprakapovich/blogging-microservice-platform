package com.lprakapovich.blog.publicationservice.api.dto;

import lombok.Data;

@Data
public class UpdateBlogDto {

    private String displayName;
    private String description;
}
