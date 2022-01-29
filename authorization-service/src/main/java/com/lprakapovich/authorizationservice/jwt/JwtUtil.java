package com.lprakapovich.authorizationservice.jwt;

import com.lprakapovich.authorizationservice.exception.JwtException;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.lprakapovich.authorizationservice.exception.JwtException.Cause.EXPIRED;
import static com.lprakapovich.authorizationservice.exception.JwtException.Cause.INVALID;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties properties;

    private String secretKey;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(properties.getSecret().getBytes());
    }

    public String generateToken(String username) {
        return doGenerateToken(new HashMap<>(), username);
    }

    private String doGenerateToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + properties.getExpirationInMillis() * 1000))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public <T> T getClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public String getUsernameFromToken(String token) {
        return getClaims(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaims(token, Claims::getExpiration);
    }

    public boolean isTokenValid(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            throw new JwtException(EXPIRED);
        } catch (io.jsonwebtoken.JwtException e) {
            e.printStackTrace();
            throw new JwtException(INVALID);
        }
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }
}
