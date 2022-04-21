package com.lprakapovich.blog.publicationservice.util;

import com.lprakapovich.blog.publicationservice.feign.AuthorizationClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.lprakapovich.blog.publicationservice.util.BlogUtil.USERNAME;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

public class AuthenticationMockUtils {

    public static final String AUTH_HEADER = "Authorization";
    public static final String TOKEN = "Bearer mockToken";

    public static void mockSuccessfulTokenValidation(AuthorizationClient client) {
        ResponseEntity<String> success = ResponseEntity.ok(USERNAME);
        given(client.validateToken(anyString())).willReturn(success);
    }

    public static void mockFailedTokenValidation(AuthorizationClient client) {
        ResponseEntity<String> failure = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        given(client.validateToken(anyString())).willReturn(failure);
    }
}
