package com.lprakapovich.authorizationservice.feign;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserClientConfiguration {

    @Bean
    public UserClientErrorDecoder userClientErrorDecoder() {
        return new UserClientErrorDecoder();
    }
}
