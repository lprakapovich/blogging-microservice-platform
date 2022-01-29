package com.lprakapovich.userservice.repository;

import com.lprakapovich.userservice.doman.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    Optional<User> getByUsername(String username);
}
