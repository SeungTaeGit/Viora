package com.viora.security.oauth;

import org.springframework.stereotype.Component;
import java.util.Map;

@Component("google") // 이 전문가의 이름(Bean 이름)을 "google"로 지정
public class GoogleUserInfoExtractor implements OAuth2UserInfoExtractor {

    @Override
    public String getEmail(Map<String, Object> attributes) {
        return (String) attributes.get("email");
    }

    @Override
    public String getNickname(Map<String, Object> attributes) {
        // 구글은 기본 닉네임을 제공하지 않으므로, 이름(name)이나 고유번호(sub)로 임시 닉네임 생성
        return "Google_" + attributes.get("sub").toString().substring(0, 8);
    }
}