package com.viora.config;

import com.viora.security.JwtAuthenticationFilter;
import com.viora.security.JwtTokenProvider;
import com.viora.config.OAuth2LoginSuccessHandler;
import com.viora.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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

    /**
     * CORS 설정을 위한 Bean (프론트엔드 연동에 필수)
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:5174"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 적용
        return source;
    }

    /**
     * API 경로 보안 필터 체인 (JWT 인증 필요)
     */
    @Bean
    @Order(2) // 두 번째 순서로 적용
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .securityMatcher("/api/**") // 이 필터는 /api/ 로 시작하는 경로에만 적용
                .httpBasic(config -> config.disable())
                .csrf(config -> config.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // CORS Preflight 요청 허용
                        .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll() // 리뷰 조회는 누구나
                        .anyRequest().authenticated() // 그 외 /api/** 는 인증 필요
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                );
        return http.build();
    }

    /**
     * 인증(Auth) 경로 보안 필터 체인 (JWT 인증 불필요)
     */
    @Bean
    @Order(1) // 첫 번째 순서로 적용
    public SecurityFilterChain authFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .securityMatcher("/auth/**", "/login/**", "/oauth2/**") // 인증 관련 경로에만 적용
                .httpBasic(config -> config.disable())
                .csrf(config -> config.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // CORS Preflight 요청 허용
                        .anyRequest().permitAll() // 이 경로들은 모두 허용
                )
                .oauth2Login(oauth2 -> oauth2 // OAuth2 설정은 여기에만 적용
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2LoginSuccessHandler)
                )
                .formLogin(config -> config.disable()); // Spring 기본 폼 로그인 비활성화

        return http.build();
    }

}

