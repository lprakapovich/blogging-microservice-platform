package com.lprakapovich.blog.publicationservice.api.dto;

import com.lprakapovich.blog.publicationservice.model.Category;
import com.lprakapovich.blog.publicationservice.model.Status;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UpdatePublicationDto {

    @NotBlank(message = "Publication title cannot be blank")
    private String title;

    @NotBlank(message = "Publication content cannot me blank")
    private String content;

    private Category category;

    @NotNull(message = "Publication must have an assigned status - Published, Draft or Hidden")
    private Status status;
}
