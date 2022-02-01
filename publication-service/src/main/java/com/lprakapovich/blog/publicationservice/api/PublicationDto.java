package com.lprakapovich.blog.publicationservice.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lprakapovich.blog.publicationservice.model.Category;
import com.lprakapovich.blog.publicationservice.model.Status;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
class PublicationDto {

    private long id;

    @NotBlank(message = "Publication header cannot be blank")
    private String header;

    private String subHeader;

    private Category category;

    @NotNull(message = "Publication must have an assigned status")
    private Status status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdDateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime lastUpdateDateTime;
}
