package com.example.gateway.route;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class GatewayRouteConfig {

    private static final String ALL = "/**";

    private static final String AUTH_SERVER_PATH = "/auth-service" + ALL;
    private static final String AUTH_SERVER_URI = "http://localhost:9090";

    private static final String USER_SERVICE_PATH = "/user-service" + ALL;
    private static final String USER_SERVICE_URI = "http://localhost:9091";

    private static final String PUBLICATION_SERVICE_PATH = "/publication-service" + ALL;
    private static final String PUBLICATION_SERVICE_URI = "http://localhost:9094";

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path(AUTH_SERVER_PATH)
                        .uri(AUTH_SERVER_URI))
                .route(p -> p
                        .path(USER_SERVICE_PATH)
                        .uri(USER_SERVICE_URI))
                .route(p -> p
                        .path(PUBLICATION_SERVICE_PATH)
                        .uri(PUBLICATION_SERVICE_URI))
                .build();
    }
}
