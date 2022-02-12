package com.lprakapovich.blog.publicationservice.api.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UpdateBlogDto {

    @NotBlank(message = "Description cannot be blank")
    private String description;
}
