package com.lprakapovich.blog.publicationservice.util;

import com.lprakapovich.blog.publicationservice.feign.AuthorizationClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

public class AuthenticationMockUtils {

    public static final String TOKEN = "Bearer mockToken";
    public static final String DEFAULT_PRINCIPAL = "principal";

    public static void mockTokenValidationWithDefaultPrincipal(AuthorizationClient client) {
        ResponseEntity<String> success = ResponseEntity.ok(DEFAULT_PRINCIPAL);
        given(client.validateToken(anyString())).willReturn(success);
    }

    public static HttpHeaders getAuthorizationHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, TOKEN);
        return headers;
    }
}
