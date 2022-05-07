package com.lprakapovich.blog.publicationservice.feign;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthenticationServerClientConfiguration {

    @Bean
    public AuthenticationServerClientErrorDecoder userClientErrorDecoder() {
        return new AuthenticationServerClientErrorDecoder();
    }
}
