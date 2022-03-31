package com.example.gateway.filter;

import com.example.gateway.route.RouteValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@RefreshScope
@Component
@Order(1)
public class AuthenticationFilter implements GlobalFilter {

    @Autowired
    private RouteValidator routeValidator;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();

        if (routeValidator.getSecurityPredicate().test(request)) {
            if (isAuthHeaderMissing(request)) {
                return missingAuthHeader(exchange);
            } else if (notBearerAuthentication(request)) {
                return notSupportedAuthHeader(exchange);
            }
        }
        return chain.filter(exchange);
    }

    private boolean notBearerAuthentication(ServerHttpRequest request) {
        List<String> authorizationHeaders = request.getHeaders().get(AUTHORIZATION_HEADER);
        return Objects.isNull(authorizationHeaders) || authorizationHeaders.isEmpty() || !authorizationHeaders.get(0).startsWith(BEARER);
    }

    private boolean isAuthHeaderMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey(AUTHORIZATION_HEADER);
    }

    private Mono<Void> missingAuthHeader(ServerWebExchange exchange) {
        return this.onError(exchange, "Authorization header is missing in request", HttpStatus.UNAUTHORIZED);
    }

    private Mono<Void> notSupportedAuthHeader(ServerWebExchange exchange) {
        return this.onError(exchange, "Not JWT authentication is not supported, request a token", HttpStatus.UNAUTHORIZED);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String s, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }
}
