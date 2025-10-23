package com.viora.config;

import com.viora.security.JwtAuthenticationFilter;
import com.viora.security.JwtTokenProvider;
import com.viora.config.OAuth2LoginSuccessHandler; // 패키지 경로 확인 필요
import com.viora.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final JwtTokenProvider jwtTokenProvider;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    // CORS 설정을 위한 Bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:5174")); // 프론트엔드 주소 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // API 경로 보안 필터 체인
    @Bean
    @Order(2)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        // 공통 보안 설정 적용 (CSRF, Session 등 비활성화)
        applyCommonSecuritySettings(http);

        http
                .securityMatcher("/api/**") // API 경로에만 적용
                // JWT 필터 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                // API 경로 권한 설정
                .authorizeHttpRequests(this::configureApiAuthorization) // 메서드 참조 사용
                // 예외 처리 설정
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                );
        return http.build();
    }

    // 인증(Auth) 경로 보안 필터 체인
    @Bean
    @Order(1)
    public SecurityFilterChain authFilterChain(HttpSecurity http) throws Exception {
        // 공통 보안 설정 적용 (CSRF, Session 등 비활성화)
        applyCommonSecuritySettings(http);

        http
                .securityMatcher("/auth/**", "/login/**", "/oauth2/**") // 인증 관련 경로에만 적용
                // 인증 경로 권한 설정 (모두 허용)
                .authorizeHttpRequests(this::configureAuthAuthorization) // 메서드 참조 사용
                // OAuth2 로그인 설정
                .oauth2Login(this::configureOAuth2Login); // 메서드 참조 사용

        return http.build();
    }

    // --- Private Helper Methods ---

    /**
     * CSRF 비활성화, 세션 STATELESS 등 공통 보안 설정을 적용합니다.
     */
    private void applyCommonSecuritySettings(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화 (람다 표현식 간소화)
                .httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // 폼 로그인 비활성화
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // 세션 STATELESS
    }

    /**
     * API 경로(/api/**)에 대한 접근 권한을 설정합니다.
     */
    private void configureApiAuthorization(org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize) {
        authorize
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Preflight 요청 허용
                .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll() // 리뷰 조회는 누구나
                .anyRequest().authenticated(); // 나머지 API는 인증 필요
    }

    /**
     * 인증 경로(/auth/** 등)에 대한 접근 권한을 설정합니다.
     */
    private void configureAuthAuthorization(org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize) {
        authorize
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Preflight 요청 허용
                .anyRequest().permitAll(); // 인증 관련 경로는 모두 허용
    }

    /**
     * OAuth2 로그인 관련 설정을 적용합니다.
     */
    private void configureOAuth2Login(org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer<HttpSecurity> oauth2) {
        oauth2
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(customOAuth2UserService) // 사용자 정보 처리 서비스
                )
                .successHandler(oAuth2LoginSuccessHandler); // 로그인 성공 핸들러
    }


    // 비밀번호 암호화를 위한 Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
