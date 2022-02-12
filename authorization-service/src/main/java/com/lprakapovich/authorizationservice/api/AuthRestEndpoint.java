package com.lprakapovich.authorizationservice.api;

import com.lprakapovich.authorizationservice.api.dto.AuthDto;
import com.lprakapovich.authorizationservice.api.dto.LoginDto;
import com.lprakapovich.authorizationservice.api.dto.RegisterDto;
import com.lprakapovich.authorizationservice.exception.JwtException;
import com.lprakapovich.authorizationservice.feign.UserClient;
import com.lprakapovich.authorizationservice.jwt.JwtUtil;
import com.lprakapovich.authorizationservice.security.ApplicationPasswordEncoder;
import com.lprakapovich.authorizationservice.service.AuthService;
import io.jsonwebtoken.Claims;
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
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final ApplicationPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    ResponseEntity<AuthDto> login(@Valid @RequestBody LoginDto request) {
        authService.authenticate(request.getUsername(), request.getPassword());
        String token = jwtUtil.generateToken(request.getUsername());
        return ResponseEntity.ok(new AuthDto(token));
    }

    @PostMapping(value = "/register")
    public ResponseEntity<AuthDto> register(@Valid @RequestBody RegisterDto request) {
        request.setPassword(encrypt(request.getPassword()));
        ResponseEntity<?> userCreatedResponse = userClient.createUser(request);
        URI createdResourceLocation = userCreatedResponse.getHeaders().getLocation();
        String token = jwtUtil.generateToken(request.getUsername());
        assert createdResourceLocation != null;
        return ResponseEntity.created(createdResourceLocation).body(new AuthDto(token));
    }

    @PostMapping("/validate")
    ResponseEntity<String> validateToken(@RequestParam String token) {
        // TODO handle builtin exceptions thrown during claims resolution
        if (!jwtUtil.isTokenValid(token)) {
            throw new JwtException(INVALID);
        }
        Claims userClaims = jwtUtil.getAllClaimsFromToken(token);
        return ResponseEntity.ok(userClaims.getSubject());
    }

    private String encrypt(String password) {
        return passwordEncoder.toEncryptedPassword(password);
    }
}
