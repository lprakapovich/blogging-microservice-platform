package com.example.gateway.route;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class GatewayRouteConfig {
    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/auth/**")
                        .uri("http://localhost:9090"))
                .route(p -> p
                        .path("/users/**")
                        .uri("http://localhost:9091"))
                .route(p -> p
                        .path("/publications/**")
                        .uri("http://localhost:9094"))
                .build();
    }
}
