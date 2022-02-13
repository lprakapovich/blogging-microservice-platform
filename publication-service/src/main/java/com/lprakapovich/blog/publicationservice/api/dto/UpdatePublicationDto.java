package com.lprakapovich.blog.publicationservice.api.dto;

import com.lprakapovich.blog.publicationservice.model.Category;
import com.lprakapovich.blog.publicationservice.model.Content;
import com.lprakapovich.blog.publicationservice.model.Status;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UpdatePublicationDto {

    @NotBlank(message = "Publication header cannot be blank")
    private String header;

    private String subHeader;

    private Category category;

    @NotNull(message = "Publication must have an assigned status - Published, Draft or Hidden")
    private Status status;

    @Valid
    @NotNull(message = "Publication content cannot me blank")
    private Content content;
}
