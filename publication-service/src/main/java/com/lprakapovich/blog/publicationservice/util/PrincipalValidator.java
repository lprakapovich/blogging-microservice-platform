package com.lprakapovich.blog.publicationservice.util;

import com.lprakapovich.blog.publicationservice.exception.PrincipalMismatchException;

import static com.lprakapovich.blog.publicationservice.util.AuthenticatedUserResolver.resolvePrincipal;

public class PrincipalValidator {

    private PrincipalValidator() {}

    public static void validate(String principalFromPath) {
        String principalFromToken = resolvePrincipal();
        if (!principalFromPath.equals(principalFromToken)) {
            throw new PrincipalMismatchException();
        }
    }
}
