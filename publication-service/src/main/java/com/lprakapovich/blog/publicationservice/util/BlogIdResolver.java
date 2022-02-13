package com.lprakapovich.blog.publicationservice.util;

import com.lprakapovich.blog.publicationservice.security.SecurityContextHolderSubjectResolver;

public class BlogIdResolver {

    private BlogIdResolver() {}

    public static String resolveUsernameFromPrincipal() {
        return SecurityContextHolderSubjectResolver.getPrincipalSubject();
    }
}
