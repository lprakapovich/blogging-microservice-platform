package com.lprakapovich.userservice.event;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UserCreatedEvent {

    private String username;
    private String firstName;
    private String lastName;

    public UserCreatedEvent(UserCreatedDetails userCreatedDetails) {
        this.username = userCreatedDetails.getUsername();
        this.firstName = userCreatedDetails.getFirstName();
        this.lastName = userCreatedDetails.getLastName();
    }
}
