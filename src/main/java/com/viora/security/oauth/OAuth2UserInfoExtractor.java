package com.viora.security.oauth;

import java.util.Map;

// 모든 소셜 로그인 정보 추출 전문가가 가져야 할 자격증(메서드)
public interface OAuth2UserInfoExtractor {
    String getEmail(Map<String, Object> attributes);
    String getNickname(Map<String, Object> attributes);
}