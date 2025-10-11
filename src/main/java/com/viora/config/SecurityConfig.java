package com.viora.config;

// src/main/java/com/viora/config/SecurityConfig.java

import com.viora.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // http 기본 인증 비활성화, REST API이므로 사용 안함
                .httpBasic(config -> config.disable())
                // csrf 보안 비활성화, REST API이므로 사용 안함
                .csrf(config -> config.disable())
                // 세션을 사용하지 않음 (JWT 방식이므로)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(authorize -> authorize
                        // 허용할 경로들을 모두 나열
                        .requestMatchers("/auth/**", "/login/**", "/oauth2/**").permitAll()
                        // 가장 마지막에 "나머지 모든 요청"에 대한 규칙을 설정
                        .anyRequest().authenticated()
                )
        // JWT 필터 추가 예정
                // OAuth2 로그인 설정 추가
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService) // 로그인 성공 후 사용자 정보를 처리할 서비스
                        )
                        // 로그인 성공/실패 핸들러 추가 예정
                );

        return http.build();
    }

    // 비밀번호 암호화를 위한 Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}