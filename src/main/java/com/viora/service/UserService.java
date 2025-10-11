package com.viora.service;

// src/main/java/com/viora/service/UserService.java
import com.viora.entity.Provider;
import com.viora.entity.User;
import com.viora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 암호화 로직을 위해 필요

    @Transactional
    public User saveUser(String email, String password, String nickname) {
        // 이메일 중복 검사
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        User newUser = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password)) // 비밀번호 암호화
                .nickname(nickname)
                .provider(Provider.VIORA)
                .build();

        return userRepository.save(newUser);
    }

    // findByEmail 메서드도 여기에 있어야 합니다.
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}