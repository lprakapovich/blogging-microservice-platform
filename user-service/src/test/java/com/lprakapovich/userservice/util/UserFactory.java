package com.lprakapovich.userservice.util;

import com.lprakapovich.userservice.domain.User;

public class UserFactory {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    public static User createDefaultUser() {
        User user = new User();
        user.setPassword(PASSWORD);
        user.setUsername(USERNAME);
        return user;
    }
}
