package com.lprakapovich.authorizationservice.service;

import com.lprakapovich.authorizationservice.api.dto.RegisterDto;
import com.lprakapovich.authorizationservice.exception.DisabledUserException;
import com.lprakapovich.authorizationservice.exception.InvalidCredentialsException;
import com.lprakapovich.authorizationservice.feign.UserClient;
import com.lprakapovich.authorizationservice.security.ApplicationPasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final ApplicationPasswordEncoder passwordEncoder;
    private final UserClient userClient;

    public void authenticate(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new DisabledUserException();
        } catch(BadCredentialsException e) {
            throw new InvalidCredentialsException();
        }
    }

    public URI register(RegisterDto request) {
        request.setPassword(encrypt(request.getPassword()));
        ResponseEntity<?> registeredResponse = userClient.createUser(request);
        return registeredResponse.getHeaders().getLocation();
    }

    private String encrypt(String password) {
        return passwordEncoder.toEncryptedPassword(password);
    }
}
