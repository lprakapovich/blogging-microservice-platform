package com.lprakapovich.blog.publicationservice.api.dto;

import com.lprakapovich.blog.publicationservice.model.Category;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PublicationDto {

    @NotBlank(message = "Publication header cannot be blank")
    private String header;

    private String subHeader;

    private Category category;
}
