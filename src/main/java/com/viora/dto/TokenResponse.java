package com.viora.dto;

// src/main/java/com/viora/dto/TokenResponse.java

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor // final 필드에 대한 생성자를 만들어줍니다.
public class TokenResponse {

    private final String accessToken;

}

/* Spring이 아닌 DTO를 사용하는 이유.
- 나중에 RefreshToken이나 사용자 닉네임 등 토큰과 함께 더 많은 정보를 보내야 할 때, 이 클래스에 필드만 추가하면 되므로 확장성이 매우 좋다.
 */