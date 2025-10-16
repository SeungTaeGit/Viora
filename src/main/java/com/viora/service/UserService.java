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

        /* userRepository.save(user); 코드는 나중에 버그나 오류를 일으키지 않는다.

        안전성: 이 코드는 JPA에게 "이 user 객체의 현재 상태를 데이터베이스에 무조건 반영해" 라고 명확하게 지시. 의도가 코드에 직접 드러나기 때문에, 누가 봐도 이해하기 쉽고 확실하게 동작.

        성능: "한 번 조회하고 또 저장하니 DB에 두 번 접근해서 느리지 않을까?" 라고 생각할 수 있지만, 그렇지 않다. JPA는 이미 DB에서 조회해온 '관리 상태'의 객체에 대해 save()를 호출하면, INSERT가 아닌 UPDATE 쿼리를 한 번만 실행. 즉, '변경 감지'가 동작했을 때와 성능 차이가 거의 없다.
         */
    }
}