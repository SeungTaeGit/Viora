package com.viora.service;

import com.viora.dto.LoginRequest;
import com.viora.dto.TokenResponse;
import com.viora.entity.User;
import com.viora.repository.UserRepository;
import com.viora.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.viora.dto.FindEmailRequest;
import com.viora.dto.ResetPasswordRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail());

        return new TokenResponse(accessToken);
    }

    /**
     * 이메일 찾기
     */
    public String findEmail(FindEmailRequest request) {
        User user = userRepository.findByNickname(request.getNickname())
                .orElseThrow(() -> new IllegalArgumentException("해당 닉네임의 사용자가 없습니다."));

        return maskEmail(user.getEmail());
    }

    /**
     * 비밀번호 재설정
     */
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmailAndNickname(request.getEmail(), request.getNickname())
                .orElseThrow(() -> new IllegalArgumentException("입력하신 정보와 일치하는 사용자가 없습니다."));

        user.changePassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user); // 명시적 저장
    }

    private String maskEmail(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex <= 3) return email;
        return email.substring(0, 3) + "***" + email.substring(atIndex);
    }
}
