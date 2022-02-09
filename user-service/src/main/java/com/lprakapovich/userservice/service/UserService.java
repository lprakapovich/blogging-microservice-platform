package com.lprakapovich.userservice.service;

import com.lprakapovich.userservice.doman.User;
import com.lprakapovich.userservice.event.UserCreatedDetails;
import com.lprakapovich.userservice.event.UserCreatedEvent;
import com.lprakapovich.userservice.exception.UserNotFoundException;
import com.lprakapovich.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public Long createUser(User user) {
        Long id = userRepository.save(user).getId();
        applicationEventPublisher.publishEvent(new UserCreatedEvent(new UserCreatedDetails(user)));
        return id;
    }

    public User getByUsername(String username) {
        return userRepository.getByUsername(username).orElseThrow(UserNotFoundException::new);
    }
}
