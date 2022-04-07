package com.lprakapovich.blog.publicationservice.api.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateBlogDto {

    @NotBlank
    private String id;
}
