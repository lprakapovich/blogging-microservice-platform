package com.lprakapovich.userservice.repository;

import com.lprakapovich.userservice.domain.User;
import com.lprakapovich.userservice.util.UserFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        assertThat(userRepository).isNotNull();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void whenUserIsPresent_checkingByUsernameReturnsTrue() {

        // given
        User user = UserFactory.createDefaultUser();
        String username = user.getUsername();

        // when
        userRepository.save(user);

        // then
        assertThat(userRepository.existsByUsername(username)).isTrue();
    }

    @Test
    void whenUserIsNotPresent_checkingByUsernameReturnsFalse() {

        // given
        // when
        // then
        assertThat(userRepository.existsByUsername(UserFactory.USERNAME)).isFalse();
    }

    @Test
    void whenUserIsPresent_gettingByUsernameReturnsProperResult() {

        // given
        User user = UserFactory.createDefaultUser();
        String username = user.getUsername();
        String password = user.getPassword();

        // when
        userRepository.save(user);
        Optional<User> optionalUser = userRepository.getByUsername(username);

        // then
        assertThat(optionalUser).isPresent();
        assertThat(optionalUser.get().getId()).isNotNull();
        assertThat(optionalUser.get().getPassword()).isEqualTo(password);
        assertThat(optionalUser.get().getUsername()).isEqualTo(username);
    }

    @Test
    void whenUserIsNotPresent_gettingByUsernameReturnsEmptyOptional() {

        // given
        // when
        // then
        assertThat(userRepository.getByUsername(UserFactory.USERNAME)).isEmpty();
    }
}
