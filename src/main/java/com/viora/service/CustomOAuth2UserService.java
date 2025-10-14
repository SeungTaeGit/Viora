package com.viora.service;

import com.viora.common.Constants;
import com.viora.entity.Provider;
import com.viora.entity.User;
import com.viora.repository.UserRepository;
import com.viora.security.oauth.OAuth2UserInfoExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    // Spring이 "google", "kakao" 라는 이름으로 등록된 모든 전문가(Extractor) Bean을 주입해줍니다.
    private final Map<String, OAuth2UserInfoExtractor> extractors;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 기본 OAuth2UserService를 통해 사용자 정보를 가져옵니다.
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 2. 어떤 소셜 로그인인지 registrationId로 구분합니다 (예: "google", "kakao")
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 3. registrationId에 맞는 전문가(Extractor)를 Map에서 찾아옵니다.
        OAuth2UserInfoExtractor extractor = extractors.get(registrationId);
        if (extractor == null) {
            throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다: " + registrationId);
        }

        // 4. 전문가에게 정보 추출을 위임합니다.
        String email = extractor.getEmail(attributes);
        String nickname = extractor.getNickname(attributes);

        // 5. 추출된 이메일로 DB에서 사용자를 찾아보고, 없으면 새로 생성(회원가입)합니다.
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .nickname(nickname)
                            .passwordHash(Constants.OAUTH_USER_PASSWORD_PLACEHOLDER)
                            .provider(Provider.valueOf(registrationId.toUpperCase()))
                            .build();
                    return userRepository.save(newUser);
                });

        // 6. Spring Security가 내부적으로 사용할 인증 정보 객체를 생성하여 반환합니다.
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "email"); // 사용자 이름(principal)으로 사용할 속성의 키
    }
}