package com.viora.dto;

// src/main/java/com/viora/dto/SignUpRequest.java

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor // JSON 데이터를 객체로 변환하기 위해 기본 생성자가 필요합니다.
public class SignUpRequest {

    private String email;
    private String password;
    private String nickname; // 닉네임 필드 추가

}