package com.example.gateway.route;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    private static final String REGISTER_URL = "/auth-service/register";
    private static final String LOGIN_URL = "/auth-service/login";
    private static final String VALIDATE_URL = "/auth-service/validate";

    private static final List<String> openApiEndpoints = List.of(REGISTER_URL, LOGIN_URL, VALIDATE_URL);

    public Predicate<ServerHttpRequest> mustBeSecured = httpServletRequest ->
            openApiEndpoints.stream().noneMatch(uri ->
                    httpServletRequest.getURI().getPath().contains(uri));
}
