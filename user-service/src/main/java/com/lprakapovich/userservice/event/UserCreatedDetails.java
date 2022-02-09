package com.lprakapovich.userservice.event;

import com.lprakapovich.userservice.doman.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserCreatedDetails {

    private String username;
    private String firstName;
    private String lastName;

    public UserCreatedDetails(User user) {
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
    }
}
