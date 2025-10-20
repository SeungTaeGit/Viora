package com.viora.dto;

// src/main/java/com/viora/dto/LoginRequest.java

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor // JSON <-> 객체 변환을 위한 기본 생성자
public class LoginRequest {

    private String email;
    private String password;

}