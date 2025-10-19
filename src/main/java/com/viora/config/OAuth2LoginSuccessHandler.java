package com.viora.config;

import com.viora.repository.UserRepository;
import com.viora.security.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // OAuth2User로 캐스팅하여 사용자 정보를 빼옵니다.
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // CustomOAuth2UserService에서 DB에 저장한 이메일을 가져옵니다.
        String email = oAuth2User.getAttribute("email");

        // 이메일로 우리 서비스의 JWT 토큰을 생성합니다.
        String accessToken = jwtTokenProvider.createAccessToken(email);

        // 프론트엔드로 리다이렉트할 URL을 만듭니다. 토큰을 쿼리 파라미터로 추가합니다.
        // 예: http://localhost:5173/oauth-redirect?token=jwt_token_here
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:5173/oauth-redirect")
                .queryParam("token", accessToken)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();

        // 생성된 URL로 사용자를 리다이렉트시킵니다.
        response.sendRedirect(targetUrl);
    }
}