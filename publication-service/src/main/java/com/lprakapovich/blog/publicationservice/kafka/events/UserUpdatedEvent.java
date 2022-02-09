package com.lprakapovich.blog.publicationservice.kafka.events;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserUpdatedEvent {

    private String firstName;
    private String lastName;
}
