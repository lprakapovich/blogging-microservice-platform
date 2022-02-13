package com.lprakapovich.authorizationservice.service;

import com.lprakapovich.authorizationservice.domain.UserData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final String username;
    private final String password;
    private final Set<? extends GrantedAuthority> authorities;
    private final boolean isAccountNonExpired;
    private final boolean isAccountNonLocked;
    private final boolean isCredentialsNonExpired;
    private final boolean isEnabled;

    public UserDetailsImpl(String username, String password) {
        this(username, password, Collections.emptySet(), true, true, true, true);
    }

    public UserDetailsImpl(UserData userData) {
        this(userData.getUsername(), userData.getPassword());
    }
}
