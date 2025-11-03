package com.viora.service;

import com.viora.dto.MyProfileResponse;
import com.viora.dto.ProfileUpdateRequest;
import com.viora.entity.User;
import com.viora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
     * 회원가입
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

        return userRepository.save(newUser);
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