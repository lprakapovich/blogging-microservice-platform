package com.lprakapovich.blog.publicationservice.feign;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthorizationClientConfiguration {

    @Bean
    public AuthorizationClientErrorDecoder userClientErrorDecoder() {
        return new AuthorizationClientErrorDecoder();
    }
}
