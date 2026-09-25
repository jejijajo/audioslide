package com.audioslide.auth.service;

import com.audioslide.auth.dto.UserResponseDto;
import com.audioslide.auth.entity.User;
import com.audioslide.auth.exception.UserNotFoundException;
import com.audioslide.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findOrCreateByEmail(String email, String username, String googleId) {
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUsername(username);
                    newUser.setRoles(Set.of("USER"));
                    newUser.setProviderId(googleId);

                    log.info("Created new user entity");

                    return userRepository.save(newUser);
                });

        log.info("User found");
        return user;
    }
}
