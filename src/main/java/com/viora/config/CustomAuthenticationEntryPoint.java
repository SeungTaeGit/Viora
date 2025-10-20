package com.viora.config; // 또는 com.viora.security 패키지

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 응답 상태 코드를 401 Unauthorized (인증되지 않음)으로 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // 응답의 컨텐츠 타입을 JSON으로, 문자 인코딩을 UTF-8로 설정
        response.setContentType("application/json;charset=UTF-8");
        // 클라이언트에게 보낼 JSON 에러 메시지를 작성
        response.getWriter().write("{\"error\": \"인증이 필요한 서비스입니다.\"}");
    }
}