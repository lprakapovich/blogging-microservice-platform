package com.lprakapovich.blog.publicationservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum Status {

    PUBLISHED("Published"),
    DRAFT("Draft"),
    SCHEDULED("Scheduled");

    private final String n;

    public static Status resolveByName(String name) {
       return Stream.of(Status.values())
                .filter(c -> c.getN().equals(name))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
