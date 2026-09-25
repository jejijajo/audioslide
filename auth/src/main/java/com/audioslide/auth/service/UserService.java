package com.audioslide.auth.service;

import com.audioslide.auth.dto.UserResponseDto;
import com.audioslide.auth.entity.User;
import com.audioslide.auth.exception.UserNotFoundException;
import com.audioslide.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findOrCreateByEmail(String email, String username, String googleId) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUsername(username);
                    newUser.setRoles(Set.of("USER"));
                    newUser.setProviderId(googleId);
                    return userRepository.save(newUser);
                });
    }
}
