package com.viora.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j // 로그 출력을 위한 Lombok 어노테이션
@Component
public class JwtTokenProvider {

    private final Key key;
    private final long accessTokenValidityInMilliseconds;
    private final UserDetailsService userDetailsService;

    /**
     * 생성자: application.properties에서 설정값 주입.
     */
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                            @Value("${jwt.expiration-in-seconds}") long expirationSeconds,
                            UserDetailsService userDetailsService) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidityInMilliseconds = expirationSeconds * 1000;
        this.userDetailsService = userDetailsService;
    }

    /**
     * 사용자 이메일을 받아 Access Token을 생성하는 메서드
     */
    public String createAccessToken(String userEmail) {
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(userEmail) // 토큰의 주체 (사용자 이메일)
                .claim("auth", "ROLE_USER") // 권한 정보 (예시)
                .setIssuedAt(new Date()) // 토큰 발급 시간
                .setExpiration(validity) // 토큰 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 사용할 암호화 알고리즘
                .compact();
    }

    /**
     * JWT 토큰을 복호화하여 Spring Security의 '인증(Authentication)' 객체를 생성하는 메서드.
     */
    public Authentication getAuthentication(String token) {
        // 1. 토큰에서 이메일(Subject)을 추출합니다.
        String email = this.getUserEmail(token);

        // 2. UserDetailsService를 사용해 DB에서 사용자 정보를 로드합니다.
        //    (이 과정이 없으면, Spring Security는 이 사용자를 '인증된' 사용자로 인정하지 않습니다.)
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // 3. Spring Security가 사용할 인증 토큰(Authentication)을 생성하여 반환합니다.
        //    (사용자 정보, 비밀번호(비워둠), 권한 목록)
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /**
     * 토큰에서 회원 정보(이메일)를 추출하는 헬퍼 메서드
     */
    public String getUserEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * 토큰의 유효성 + 만료일자를 검증하는 헬퍼 메서드
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    /**
     * 토큰을 파싱하여 클레임(정보)을 추출.
     */
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}