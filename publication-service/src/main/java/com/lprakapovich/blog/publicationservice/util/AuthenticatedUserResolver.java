package com.lprakapovich.blog.publicationservice.util;

import com.lprakapovich.blog.publicationservice.security.SecurityContextHolderSubjectResolver;

public class AuthenticatedUserResolver {

    private AuthenticatedUserResolver() {}

    public static String resolveUsernameFromPrincipal() {
        return SecurityContextHolderSubjectResolver.getPrincipalSubject();
    }
}
