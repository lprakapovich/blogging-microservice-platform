package com.lprakapovich.blog.publicationservice.deserialization;

import com.lprakapovich.blog.publicationservice.model.Status;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

// TODO not used by spring for some reason
@Component
public class StatusConverter implements Converter<String, Status> {
    @Override
    public Status convert(@NotNull String statusName) {
        return Status.resolveByName(statusName);
    }
}
