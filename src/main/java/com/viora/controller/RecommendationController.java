package com.viora.controller;

import com.viora.dto.ReviewResponse; // ReviewResponse DTO import
import com.viora.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono; // WebClient 사용 시 Mono import

import java.util.List;

@RestController
@RequestMapping("/api/reviews") // 기존 ReviewController와 경로가 겹치지 않도록 주의
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    /**
     * 현재 로그인한 사용자를 위한 맞춤 리뷰 추천 API
     * GET /api/reviews/recommended
     */
    @GetMapping("/recommended")
    public Mono<ResponseEntity<List<ReviewResponse>>> getRecommendedReviews(
            @AuthenticationPrincipal UserDetails userDetails) {

        // 현재 로그인한 사용자의 이메일 가져오기
        String userEmail = userDetails.getUsername();

        // RecommendationService 호출하여 추천 리뷰 목록 (Mono) 받아오기
        return recommendationService.getRecommendations(userEmail)
                .map(ResponseEntity::ok) // Mono<List<ReviewResponse>> -> Mono<ResponseEntity<List<ReviewResponse>>>
                .defaultIfEmpty(ResponseEntity.notFound().build()); // 결과가 비어있으면 404 응답
    }
}
