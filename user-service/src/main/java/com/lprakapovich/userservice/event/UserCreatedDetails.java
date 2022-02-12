package com.lprakapovich.userservice.event;

import com.lprakapovich.userservice.doman.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserCreatedDetails {

    private String username;

    public UserCreatedDetails(User user) {
        this.username = user.getUsername();
    }
}
