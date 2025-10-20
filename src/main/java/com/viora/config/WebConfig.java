//package com.viora.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**") // 모든 경로에 대해
//                .allowedOrigins("http://localhost:5173") // 이 출처의 요청을 허용
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
//                .allowedHeaders("*") // 모든 헤더 허용
//                .allowCredentials(true); // 쿠키/인증 정보 허용
//    }
//}

/*
🚨 CORS 오류 해결하기 (백엔드 수정)
프론트엔드에서 로그인을 시도하면, 브라우저 콘솔(F12)에 CORS 오류가 발생.

CORS 오류란? 보안상의 이유로, 웹 브라우저는 기본적으로 다른 출처(예: localhost:5173의 프론트엔드가 localhost:8080의 백엔드를 호출)로의 API 요청을 차단.

해결책: 백엔드 서버에서 "내 API는 localhost:5173에서 오는 요청을 허용해" 라고 명시적으로 알려줘야 한다.
 */