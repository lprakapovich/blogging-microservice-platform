package com.lprakapovich.userservice.event;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UserCreatedEvent {

    private String username;
    private String blogUri;

    public UserCreatedEvent(String username, String blogUri) {
        this.username = username;
        this.blogUri = blogUri;
    }
}
