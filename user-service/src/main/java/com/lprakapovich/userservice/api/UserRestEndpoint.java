package com.lprakapovich.userservice.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.lprakapovich.userservice.doman.User;
import com.lprakapovich.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/user-service")
@RequiredArgsConstructor
public class UserRestEndpoint {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserDto userDto) {
        User user = objectMapper.convertValue(userDto, User.class);
        URI location = URI.create(userService.createUser(user).toString());
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getByUsername(username));
    }
}
