package com.lprakapovich.blog.publicationservice.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lprakapovich.blog.publicationservice.model.Status;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class PublicationDto {

    private long id;

    @NotBlank(message = "Publication title cannot be blank")
    private String title;

    @NotBlank(message = "Publication content cannot be blank")
    private String content;

    private CategoryDto category;

    @NotNull(message = "Publication must have an assigned status")
    private Status status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdDateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedDateTime;

    private BlogDto blog;
}
