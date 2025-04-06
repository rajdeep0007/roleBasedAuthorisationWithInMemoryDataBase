package com.example.jwtdemo.service;

import com.example.jwtdemo.model.User;
import com.example.jwtdemo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AdminService {
    @Autowired
    private UserRepository userRepository;

    public User addUser(User input) throws BadRequestException {
        try {
            return userRepository.findByEmail(input.getEmail())
                    .map(existingUser -> updateUser(existingUser, input))
                    .orElseGet(() -> saveNewUser(input));
        } catch (Exception e) {
            log.error("Multiple users found with email id: {}", input.getEmail(), e);
            throw new BadRequestException("Multiple users found with the same email.");
        }
    }

    private User updateUser(User existingUser, User input) {
        existingUser.setUsername(input.getUsername());
        existingUser.setPassword(new BCryptPasswordEncoder().encode(input.getPassword()));
        existingUser.setRole(input.getRole());
        return userRepository.save(existingUser);
    }


    private User saveNewUser(User input) {
        User user = new User();
        user.setUsername(input.getUsername());
        user.setEmail(input.getEmail());
        user.setPassword(new BCryptPasswordEncoder().encode(input.getPassword()));
        user.setRole(input.getRole());
        return userRepository.save(user);
    }
}
