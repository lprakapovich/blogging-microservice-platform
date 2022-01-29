package com.lprakapovich.authorizationservice.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "auth.jwt")
public class JwtProperties {

    private String secret;
    private long expirationInMillis;
}
