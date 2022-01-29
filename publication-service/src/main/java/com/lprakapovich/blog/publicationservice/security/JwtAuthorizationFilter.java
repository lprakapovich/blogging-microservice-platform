package com.lprakapovich.blog.publicationservice.security;

import com.lprakapovich.blog.publicationservice.feign.AuthorizationClient;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final AuthorizationClient authorizationClient;

    private final String AUTH_HEADER = "Authorization";
    private final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            if (authHeaderIsPresent(request)) {
                String token = getTokenFromRequest(request);
                ResponseEntity<?> responseEntity = authorizationClient.validateToken(token);
                if (response.getStatus() == 200) {
                    log.error("TOKEN OK!!");
                } else {
                    log.error("TOKEN DID NOT PASS VALIDATION");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    SecurityContextHolder.clearContext();
                }
            } else {
                log.error("TOKEN IS NOT INCLUDED INTO RESPONSE");
                SecurityContextHolder.clearContext();
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            return;
        }
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        return request.getHeader(AUTH_HEADER).replace(BEARER_PREFIX, "");
    }

    private boolean authHeaderIsPresent(HttpServletRequest request) {
        String authenticationHeader = request.getHeader(AUTH_HEADER);
        return authenticationHeader != null && authenticationHeader.startsWith(BEARER_PREFIX);
    }
}
