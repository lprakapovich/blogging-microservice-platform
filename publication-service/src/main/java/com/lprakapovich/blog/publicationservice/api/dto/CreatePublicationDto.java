package com.lprakapovich.blog.publicationservice.api.dto;

import com.lprakapovich.blog.publicationservice.model.Status;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class CreatePublicationDto {

    @NotBlank(message = "Publication title cannot be blank")
    private String title;

    private CategoryDto category;

    @NotNull(message = "Publication must have an assigned status - Published, Draft or Hidden")
    private Status status;

    @NotBlank(message = "Publication content cannot me blank")
    private String content;
}
