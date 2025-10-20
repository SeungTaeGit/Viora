package com.viora.controller;

// src/main/java/com/viora/controller/AuthController.java

import com.viora.dto.LoginRequest;
import com.viora.dto.SignUpRequest;
import com.viora.dto.TokenResponse;
import com.viora.entity.User;
import com.viora.security.JwtTokenProvider;
import com.viora.service.AuthService;
import com.viora.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 생성
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequest request) {
        // 3개의 파라미터를 모두 넘겨줍니다.
        userService.saveUser(request.getEmail(), request.getPassword(), request.getNickname());
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        User user = userService.findByEmail(request.getEmail());

        // authService의 login 메서드를 호출합니다.
        TokenResponse tokenResponse = authService.login(request);
        return ResponseEntity.ok(tokenResponse);
    }
}