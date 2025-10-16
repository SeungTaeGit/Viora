package com.viora.service;

// src/main/java/com/viora/service/UserService.java
import com.viora.entity.Provider;
import com.viora.entity.User;
import com.viora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.viora.dto.MyProfileResponse;
import com.viora.dto.ProfileUpdateRequest;

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

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    /**
     * 내 프로필 정보 조회
     */
    @Transactional(readOnly = true)
    public MyProfileResponse getMyProfile(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return new MyProfileResponse(user);
    }

    /**
     * 내 프로필 정보 수정
     */
    public void updateMyProfile(String userEmail, ProfileUpdateRequest request) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.updateProfile(
                request.getNickname(),
                request.getProfileImageUrl(),
                request.getBio()
        );

        userRepository.save(user);
    }
}