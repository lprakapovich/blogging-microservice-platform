package com.lprakapovich.blog.publicationservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Status {

    PUBLISHED("Published"),
    DRAFT("Draft"),
    SCHEDULED("Scheduled");

    private final String name;
}
