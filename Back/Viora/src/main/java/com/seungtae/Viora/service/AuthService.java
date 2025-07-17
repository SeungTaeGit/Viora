package com.seungtae.Viora.service;

import com.seungtae.Viora.dto.LoginRequest;
import com.seungtae.Viora.entity.User;
import com.seungtae.Viora.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean login(LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());
        if (userOpt.isEmpty()) return false;

        User user = userOpt.get();
        return BCrypt.checkpw(loginRequest.getPassword(), user.getPassword());
    }
}
