package com.viora.security.oauth;

import org.springframework.stereotype.Component;
import java.util.Map;

@Component("kakao") // 이 전문가의 이름(Bean 이름)을 "kakao"로 지정
public class KakaoUserInfoExtractor implements OAuth2UserInfoExtractor {

    @Override
    public String getEmail(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        return (String) kakaoAccount.get("email");
    }

    @Override
    public String getNickname(Map<String, Object> attributes) {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        return (String) properties.get("nickname");
    }
}