package com.viora.service;

import com.recombee.api_client.RecombeeClient;
import com.recombee.api_client.api_requests.SetUserValues;
import com.recombee.api_client.exceptions.ApiException;
import com.viora.dto.MyProfileResponse;
import com.viora.dto.ProfileUpdateRequest;
import com.viora.entity.User;
import com.viora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RecombeeClient recombeeClient;

    /**
     * Spring Security의 UserDetailsService 구현
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                true, true, true, true,
                user.getAuthorities()
        );
    }

    /**
     * 회원가입 (Recombee 동기화)
     */
    public User saveUser(String email, String password, String nickname) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        User newUser = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .nickname(nickname)
                .provider(com.viora.entity.Provider.VIORA)
                .build();

        User savedUser = userRepository.save(newUser);

        try {
            String userIdStr = savedUser.getId().toString();

            Map<String, Object> userValues = new HashMap<>();
            userValues.put("nickname", savedUser.getNickname());

            recombeeClient.send(new SetUserValues(userIdStr, userValues)
                    .setCascadeCreate(true)
            );
            log.info("Recombee 'SetUserValues' 동기화 성공: User ID {}", userIdStr);
        } catch (ApiException e) {
            log.error("Recombee 'SetUserValues' 동기화 실패: {}", e.getMessage());
        }

        return savedUser;
    }

    /**
     * 이메일로 사용자 찾기 (AuthService에서 사용)
     */
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));
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