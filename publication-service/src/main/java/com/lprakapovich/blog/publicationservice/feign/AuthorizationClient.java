package com.lprakapovich.blog.publicationservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Declarative API for user service used instead of RestTemplate
 */

@FeignClient(name = "authorization-service", url = "http://localhost:9090", configuration = AuthorizationClientConfiguration.class)
public interface AuthorizationClient {

    @PostMapping("/auth/validate")
    ResponseEntity<?> validateToken(@RequestParam String token);
}
