package com.lprakapovich.userservice.event;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UserCreatedEvent {

    private String username;

    public UserCreatedEvent(UserCreatedDetails userCreatedDetails) {
        this.username = userCreatedDetails.getUsername();
    }
}
