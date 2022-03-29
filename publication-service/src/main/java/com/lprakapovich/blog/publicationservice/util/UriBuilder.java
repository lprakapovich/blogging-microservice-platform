package com.lprakapovich.blog.publicationservice.util;

import java.net.URI;

public class UriBuilder {

    private UriBuilder() {}

    private static final String DELIMITER = ",";

    public static URI build(String ... args) {
        return URI.create(String.join(DELIMITER, args));
    }
}
