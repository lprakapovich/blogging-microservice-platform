package com.lprakapovich.blog.publicationservice.security;

import com.lprakapovich.blog.publicationservice.feign.AuthenticationServerClient;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final AuthenticationServerClient authenticationServerClient;

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private static final String MISSING_AUTH_HEADER = "Error:: Missing auth header";
    private static final String JWT_VALIDATION_FAILED = "Error:: Token validation failed";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            if (authHeaderIsPresent(request)) {
                String token = getTokenFromRequest(request);
                ResponseEntity<String> responseEntity = authenticationServerClient.validateToken(token);
                if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                    String principal = responseEntity.getBody();
                    setAuthentication(principal);
                } else {
                    log.error(JWT_VALIDATION_FAILED);
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    SecurityContextHolder.clearContext();
                }
            } else {
                log.error(MISSING_AUTH_HEADER);
                SecurityContextHolder.clearContext();
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }

    private void setAuthentication(String subject) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(subject, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        return request.getHeader(AUTH_HEADER).replace(BEARER_PREFIX, "");
    }

    private boolean authHeaderIsPresent(HttpServletRequest request) {
        String authenticationHeader = request.getHeader(AUTH_HEADER);
        return authenticationHeader != null && authenticationHeader.startsWith(BEARER_PREFIX);
    }
}
