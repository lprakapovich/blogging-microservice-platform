package com.lprakapovich.userservice.service;

import com.lprakapovich.userservice.doman.User;
import com.lprakapovich.userservice.exception.UserNotFoundException;
import com.lprakapovich.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Long createUser(User user) {
        return userRepository.save(user).getId();
    }

    public User getByUsername(String username) {
        return userRepository.getByUsername(username).orElseThrow(UserNotFoundException::new);
    }
}
