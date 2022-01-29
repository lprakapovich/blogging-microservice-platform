package com.lprakapovich.blog.publicationservice.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum  ContentType {

    H2("h2"),
    H3("h3"),
    PLAIN_TEXT("plain text");

    private String name;
}
