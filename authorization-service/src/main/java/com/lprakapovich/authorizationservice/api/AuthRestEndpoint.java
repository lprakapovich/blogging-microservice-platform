package com.lprakapovich.authorizationservice.api;

import com.lprakapovich.authorizationservice.api.dto.AuthDto;
import com.lprakapovich.authorizationservice.api.dto.LoginDto;
import com.lprakapovich.authorizationservice.api.dto.RegisterDto;
import com.lprakapovich.authorizationservice.exception.JwtException;
import com.lprakapovich.authorizationservice.feign.UserClient;
import com.lprakapovich.authorizationservice.jwt.JwtService;
import com.lprakapovich.authorizationservice.security.ApplicationPasswordEncoder;
import com.lprakapovich.authorizationservice.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

import static com.lprakapovich.authorizationservice.exception.JwtException.Cause.INVALID;

@RestController
@RequestMapping("/auth-service")
@RequiredArgsConstructor
public class AuthRestEndpoint {

    private final UserClient userClient;
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final ApplicationPasswordEncoder passwordEncoder;

    @PostMapping(value = "/register")
    public ResponseEntity<AuthDto> register(@Valid @RequestBody RegisterDto request) {
        request.setPassword(encrypt(request.getPassword()));
        ResponseEntity<?> userCreatedResponse = userClient.createUser(request);
        URI createdResourceLocation = userCreatedResponse.getHeaders().getLocation();
        String token = jwtService.generateToken(request.getUsername());
        assert createdResourceLocation != null;
        return ResponseEntity.created(createdResourceLocation).body(new AuthDto(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDto> login(@Valid @RequestBody LoginDto request) {
        authenticationService.authenticate(request.getUsername(), request.getPassword());
        String token = jwtService.generateToken(request.getUsername());
        return ResponseEntity.ok(new AuthDto(token));
    }

    @PostMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestParam String token) {
        // TODO handle builtin exceptions thrown during claims resolution
        if (!jwtService.isTokenValid(token)) {
            throw new JwtException(INVALID);
        }
        return ResponseEntity.ok(jwtService.getSubjectFromToken(token));
    }

    private String encrypt(String password) {
        return passwordEncoder.toEncryptedPassword(password);
    }
}
