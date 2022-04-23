package com.lprakapovich.blog.publicationservice.api.dto.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GenericMappingUtils {

    private final ObjectMapper mapper;

    public <S, T> T map(S source, Class<T> clazz) {
        return mapper.convertValue(source, clazz);
    }

    public <S, T> List<T> mapList(List<S> list, Class<T> clazz) {
        return list.stream()
                .map(b -> mapper.convertValue(b, clazz))
                .collect(Collectors.toList());
    }
}
