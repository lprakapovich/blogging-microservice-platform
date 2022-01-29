package com.lprakapovich.authorizationservice.service;

import com.lprakapovich.authorizationservice.domain.UserData;
import com.lprakapovich.authorizationservice.exception.UserNotFoundException;
import com.lprakapovich.authorizationservice.feign.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserClient userClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ResponseEntity<UserData> userResponse = userClient.getUserByUsername(username);
        if (userResponse.getStatusCode().is4xxClientError()) {
            throw new UserNotFoundException();
        }
        return new UserDetailsImpl(userResponse.getBody());
    }
}
