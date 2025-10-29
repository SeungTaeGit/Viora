package com.viora.service;

import com.viora.dto.RecommendationRequest;
import com.viora.dto.RecommendationResponse;
import com.viora.dto.ReviewResponse; // ReviewResponse import
import com.viora.entity.Review;
import com.viora.entity.User;
import com.viora.repository.ReviewLikeRepository; // ReviewLikeRepository import 추가
import com.viora.repository.ReviewRepository;
import com.viora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j // 로깅을 위한 어노테이션
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션 설정
public class RecommendationService {

    private final WebClient aiWebClient; // WebClient 주입 (주석 처리해도 유지)
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository; // isLiked 계산을 위해 추가

    /**
     * 사용자 이메일을 기반으로 AI 추천 서버에 요청하고 결과를 받아오는 메서드
     */
    public Mono<List<ReviewResponse>> getRecommendations(String userEmail) {
        // 1. 사용자 정보 조회 (Mock 데이터에서도 필요)
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. AI 서버에 보낼 요청 데이터 생성
        // User 엔티티에 getId() 메서드가 있다고 가정합니다. 없다면 추가해야 합니다.
        // RecommendationRequest requestDto = new RecommendationRequest(user.getId());

        // --- 실제 AI 서버 호출 로직 (주석 처리) ---
        /*
        return aiWebClient.post() // POST 요청
                .uri("/recommend") // application.properties에 설정된 base-url 뒤의 경로
                .bodyValue(requestDto) // 요청 본문에 데이터 담기
                .retrieve() // 응답 받기 시작
                .bodyToMono(RecommendationResponse.class) // 응답 본문을 RecommendationResponse 객체로 변환 (Mono 형태)
                .flatMap(response -> {
                    // 4. AI 서버로부터 받은 추천 리뷰 ID 목록 처리
                    List<Long> recommendedReviewIds = response.getRecommendedReviewIds();
                    if (recommendedReviewIds == null || recommendedReviewIds.isEmpty()) {
                        log.info("추천 결과 없음 (AI 서버)");
                        return Mono.just(Collections.emptyList()); // 빈 목록 반환
                    }
                    log.info("AI 추천된 리뷰 ID 목록: {}", recommendedReviewIds);

                    // 5. ID 목록으로 실제 리뷰 데이터 조회 (IN 쿼리 사용)
                    List<Review> reviews = reviewRepository.findAllById(recommendedReviewIds);

                    // 6. 조회된 리뷰들을 ReviewResponse DTO로 변환 (isLiked 포함)
                    List<ReviewResponse> reviewResponses = reviews.stream()
                            .map(review -> {
                                boolean isLiked = reviewLikeRepository.existsByUserAndReview(user, review);
                                return new ReviewResponse(review, isLiked);
                            })
                            .collect(Collectors.toList());

                    return Mono.just(reviewResponses);
                })
                .doOnError(error -> log.error("AI 추천 서버 호출 오류: {}", error.getMessage())) // 에러 로깅
                .onErrorReturn(Collections.emptyList()); // 에러 발생 시 빈 목록 반환
        */

        // --- 가짜(Mock) 데이터 반환 로직 ---
        log.info("===== 가짜 추천 데이터 반환 시작 (사용자: {}) =====", userEmail);
        List<Long> mockReviewIds = List.of(1L, 3L, 4L);

        // ID 목록으로 실제 리뷰 데이터 조회
        List<Review> reviews = reviewRepository.findAllById(mockReviewIds);
        log.info("DB에서 찾은 Mock 추천 리뷰 개수: {}", reviews.size());

        // 조회된 리뷰들을 ReviewResponse DTO로 변환 (isLiked 정보 포함)
        List<ReviewResponse> reviewResponses = reviews.stream()
                .map(review -> {
                    boolean isLiked = reviewLikeRepository.existsByUserAndReview(user, review);
                    log.debug("리뷰 ID: {}, isLiked: {}", review.getId(), isLiked); // 각 리뷰의 좋아요 상태 로깅
                    return new ReviewResponse(review, isLiked);
                })
                .collect(Collectors.toList());

        log.info("===== 가짜 추천 데이터 반환 완료 ({}개) =====", reviewResponses.size());
        // Mono 형태로 감싸서 반환 (Controller의 반환 타입과 맞춤)
        return Mono.just(reviewResponses);
    }
}

