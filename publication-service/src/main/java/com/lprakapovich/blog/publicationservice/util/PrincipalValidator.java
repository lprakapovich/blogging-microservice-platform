package com.lprakapovich.blog.publicationservice.util;

import com.lprakapovich.blog.publicationservice.exception.PrincipalMismatchException;

import static com.lprakapovich.blog.publicationservice.util.AuthenticatedUserResolver.resolveUsernameFromPrincipal;

public class PrincipalValidator {

    private PrincipalValidator() {}

    public static void validate(String principalFromPath) {
        String principalFromToken = resolveUsernameFromPrincipal();
        if (!principalFromPath.equals(principalFromToken)) {
            throw new PrincipalMismatchException();
        }
    }
}
