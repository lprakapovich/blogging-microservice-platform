package com.lprakapovich.blog.publicationservice.security;

import com.lprakapovich.blog.publicationservice.exception.SecurityProcessHolderNotInitialized;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextHolderSubjectResolver {

    private SecurityContextHolderSubjectResolver() {}

    public static String getPrincipalSubject() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        throw new SecurityProcessHolderNotInitialized();
    }
}
