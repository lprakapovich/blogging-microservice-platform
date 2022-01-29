package com.lprakapovich.authorizationservice.security;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ApplicationPasswordEncoder {

    private final PasswordEncoder passwordEncoder;

    public String toEncryptedPassword(String password) {
        return passwordEncoder.encode(password);
    }
}
