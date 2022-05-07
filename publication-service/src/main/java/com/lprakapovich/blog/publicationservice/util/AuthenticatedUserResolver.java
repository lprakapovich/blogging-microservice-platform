package com.lprakapovich.blog.publicationservice.util;

import com.lprakapovich.blog.publicationservice.security.SecurityContextPrincipalResolver;

public class AuthenticatedUserResolver {

    private AuthenticatedUserResolver() {}

    public static String resolvePrincipal() {
        return SecurityContextPrincipalResolver.getPrincipal();
    }
}
