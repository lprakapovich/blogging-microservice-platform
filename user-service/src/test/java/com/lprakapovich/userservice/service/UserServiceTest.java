package com.lprakapovich.userservice.service;

import com.lprakapovich.userservice.domain.User;
import com.lprakapovich.userservice.exception.DuplicatedUsernameException;
import com.lprakapovich.userservice.exception.UserNotFoundException;
import com.lprakapovich.userservice.repository.UserRepository;
import com.lprakapovich.userservice.util.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    private

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    @Test
    void whenUserIsCreated_modelsAreEqual() {
        // given
        User user = UserFactory.createDefaultUser();
        given(userRepository.save(any())).willReturn(user);
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        // when
        userService.createUser(user);

        // then
        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();
        assertThat(capturedUser).isEqualTo(user);
    }

    @Test
    void whenUsernameIsNotUnique_exceptionIsThrown() {
        // given
        User user = UserFactory.createDefaultUser();
        given(userRepository.existsByUsername(user.getUsername())).willReturn(true);

        // when
        // then
        assertThrows(DuplicatedUsernameException.class, () -> userService.createUser(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void whenUserIsPresent_gettingByUsernameReturnsProperResult() {
        //given
        User user = UserFactory.createDefaultUser();
        given(userRepository.getByUsername(user.getUsername())).willReturn(Optional.of(user));

        // when
        User byUsername = userService.getByUsername(user.getUsername());

        // then
        assertThat(byUsername).isEqualTo(user);
    }

    @Test
    void whenUsernameIsNotPresent_exceptionIsThrown() {
        // given
        given(userRepository.getByUsername(anyString())).willReturn(Optional.empty());

        // when
        // then
        assertThrows(UserNotFoundException.class, () -> userService.getByUsername(UserFactory.USERNAME));
    }


    @Test
    void whenUserIsPresent_existenceCheckReturnsTrue() {
        // given
        given(userRepository.existsByUsername(anyString())).willReturn(true);

        // when
        // then
        assertThat(userService.existsByUsername(UserFactory.USERNAME)).isTrue();
    }

    @Test
    void whenUserIsNotPresent_existenceCheckReturnsFalse() {
        // given
        given(userRepository.existsByUsername(anyString())).willReturn(false);

        // when
        // then
        assertThat(userService.existsByUsername(UserFactory.USERNAME)).isFalse();
    }
}
