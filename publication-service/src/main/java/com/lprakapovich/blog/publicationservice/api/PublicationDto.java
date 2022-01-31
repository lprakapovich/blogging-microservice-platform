package com.lprakapovich.blog.publicationservice.api;

import com.lprakapovich.blog.publicationservice.model.Category;
import com.lprakapovich.blog.publicationservice.model.Status;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
class PublicationDto {

    private long id;

    @NotBlank(message = "Publication header cannot be blank")
    private String header;

    private String subHeader;

    private Category category;

    private Status status;
}
