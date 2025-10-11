package com.viora.service;

// src/main/java/com/viora/service/AuthService.java

import com.viora.dto.LoginRequest;
import com.viora.dto.TokenResponse;
import com.viora.entity.User;
import com.viora.repository.UserRepository;
import com.viora.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        // 1. 이메일로 사용자 조회 (없으면 예외 발생)
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        // 2. 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3. 로그인 성공 시, JWT 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail());

        // 4. DTO에 담아 토큰 반환
        return new TokenResponse(accessToken);
    }
}
