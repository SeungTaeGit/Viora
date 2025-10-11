package com.viora.service;

// src/main/java/com/viora/service/CustomOAuth2UserService.java

import com.viora.entity.Provider;
import com.viora.entity.User;
import com.viora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 서비스 제공자 (google, kakao 등)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // 사용자 정보
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = getEmailFromAttributes(registrationId, attributes);

        // DB에 이미 있는 사용자인지 확인
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    // 없는 사용자라면 새로 생성 (회원가입)
                    // 1. 카카오가 보내준 정보 묶음(attributes)에서 필요한 정보
                    String nickname = createTemporaryNickname(attributes, registrationId);

                    // 2. 이 정보로 우리 DB에 저장할 User 객체 생성
                    User newUser = User.builder()
                            .email(email)
                            .nickname(nickname)
                            .passwordHash("OAUTH_USER") // 소셜 로그인 유저는 비밀번호가 없으므로 임시값 설정
                            .provider(Provider.valueOf(registrationId.toUpperCase())) // "kakao" -> KAKAO
                            .build();

                    // 3. DB에 저장(회원가입)하고, 그 User 객체를 반환
                    return userRepository.save(newUser);
                });

        // Spring Security가 내부적으로 사용할 인증 정보 객체
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "email"); // 사용자 이름으로 사용할 속성 키
    }


    // 각 소셜 서비스(provider)에서 받아온 사용자 정보(attributes)에서 이메일을 추출하는 메서드
    private String getEmailFromAttributes(String registrationId, Map<String, Object> attributes) {
        if ("google".equals(registrationId)) {
            return (String) attributes.get("email");
        } else if ("kakao".equals(registrationId)) {
            // 카카오는 사용자 정보가 "kakao_account"라는 Map 안에 중첩되어 있습니다.
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            return (String) kakaoAccount.get("email");
        }
        // 다른 소셜 로그인을 추가할 경우, 여기에 로직을 추가해야 합니다.
        throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다: " + registrationId);
    }

    // 닉네임은 필수값이므로, 소셜 로그인 정보에서 가져와 임시 닉네임을 만들어주는 헬퍼 메서드
    private String createTemporaryNickname(Map<String, Object> attributes, String registrationId) {
        if ("google".equals(registrationId)) {
            return "Google_" + attributes.get("sub").toString().substring(0, 8);
        } else if ("kakao".equals(registrationId)) {
            Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
            return "Kakao_" + properties.get("nickname");
        }
        return "User_" + UUID.randomUUID().toString().substring(0, 8);
    }
}