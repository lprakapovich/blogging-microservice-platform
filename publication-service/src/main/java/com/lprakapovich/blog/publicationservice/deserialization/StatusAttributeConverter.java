package com.lprakapovich.blog.publicationservice.deserialization;

import com.lprakapovich.blog.publicationservice.model.Status;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class StatusAttributeConverter implements AttributeConverter<Status, String> {

    @Override
    public String convertToDatabaseColumn(Status status) {
        if (status == null) {
            return null;
        }
        return status.getN();
    }

    @Override
    public Status convertToEntityAttribute(String statusName) {
        if (statusName == null) {
            return null;
        }
        return Status.resolveByName(statusName);
    }
}
