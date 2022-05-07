package com.lprakapovich.blog.publicationservice.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.lprakapovich.blog.publicationservice.exception.FeignClientException;
import com.lprakapovich.blog.publicationservice.exception.FeignResponseMappingException;
import com.lprakapovich.blog.publicationservice.exception.error.ErrorEnvelope;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class AuthenticationServerClientErrorDecoder implements ErrorDecoder {

    @Autowired
    private ObjectMapper objectMapper;

    private static final String FEIGN_NULL_RESPONSE_BODY = "Null response body from Feign Client";

    @Override
    public Exception decode(String s, Response response) {
        try {
            if (response.body() != null) {
                BufferedReader reader = new BufferedReader(response.body().asReader(StandardCharsets.UTF_8));
                String responseBody = reader.lines().collect(Collectors.joining("\n"));
                ErrorEnvelope errorEnvelope = objectMapper.readValue(responseBody, ErrorEnvelope.class);
                throw new FeignClientException(errorEnvelope.getMessage(), errorEnvelope.getResponseStatus());
            } else {
                throw new FeignClientException(FEIGN_NULL_RESPONSE_BODY, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException e) {
            throw new FeignResponseMappingException();
        }
    }
}
