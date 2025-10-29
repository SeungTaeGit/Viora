package com.viora.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // application.properties(or yml) 에서 AI 서버의 기본 URL을 가져옵니다. (나중에 추가 필요)
    @Value("${ai.recommend.server.base-url:http://localhost:8000}") // 기본값은 임시 로컬 주소
    private String aiServerBaseUrl;

    @Bean
    public WebClient recommendationWebClient() {
        return WebClient.builder()
                .baseUrl(aiServerBaseUrl) // AI 추천 서버의 기본 주소 설정
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) // 기본 헤더 설정
                .build();
    }
}
