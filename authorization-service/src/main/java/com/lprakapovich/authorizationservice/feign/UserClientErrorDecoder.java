package com.lprakapovich.authorizationservice.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lprakapovich.authorizationservice.exception.FeignClientException;
import com.lprakapovich.authorizationservice.exception.FeignResponseMappingException;
import com.lprakapovich.authorizationservice.exception.error.ErrorEnvelope;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class UserClientErrorDecoder implements ErrorDecoder {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public Exception decode(String s, Response response) {
        try {
            BufferedReader reader = new BufferedReader(response.body().asReader(StandardCharsets.UTF_8));
            String responseBody = reader.lines().collect(Collectors.joining("\n"));
            ErrorEnvelope errorEnvelope = objectMapper.readValue(responseBody, ErrorEnvelope.class);
            throw new FeignClientException(errorEnvelope.getMessage(), errorEnvelope.getResponseStatus());
        } catch (IOException e) {
            throw new FeignResponseMappingException();
        }
    }
}
