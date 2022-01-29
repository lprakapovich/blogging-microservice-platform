package com.lprakapovich.authorizationservice.feign;

import com.lprakapovich.authorizationservice.api.dto.RegisterDto;
import com.lprakapovich.authorizationservice.domain.UserData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Declarative API for user service used instead of RestTemplate
 */

@FeignClient(name = "user-service", url = "http://localhost:9091", configuration = UserClientConfiguration.class)
public interface UserClient {

    @PostMapping("/users")
    ResponseEntity<?> createUser(@RequestBody RegisterDto userDetails);

    @GetMapping("/users/{username}")
    ResponseEntity<UserData> getUserByUsername(@PathVariable String username);
}
