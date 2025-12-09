package com.viora.service;

import com.recombee.api_client.RecombeeClient;
import com.recombee.api_client.api_requests.RecommendItemsToUser;
import com.recombee.api_client.bindings.Recommendation;
import com.recombee.api_client.bindings.RecommendationResponse;
import com.recombee.api_client.exceptions.ApiException;
import com.viora.dto.ReviewResponse;
import com.viora.entity.Review;
import com.viora.entity.User;
import com.viora.repository.ReviewLikeRepository;
import com.viora.repository.ReviewRepository;
import com.viora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final RecombeeClient recombeeClient;

    /**
     * 사용자 이메일을 기반으로 Recombee 추천 서버에 요청하고 결과를 받아오는 메서드
     */
    public Mono<List<ReviewResponse>> getRecommendations(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        try {
            RecommendationResponse result = recombeeClient.send(
                    new RecommendItemsToUser(user.getId().toString(), 10)
                            .setScenario("viora-main-recommendation")
                            .setCascadeCreate(true)
            );

            log.info("Recombee 추천 성공: 사용자 ID {}", user.getId());

            List<Recommendation> recommendations = StreamSupport.stream(result.spliterator(), false)
                    .collect(Collectors.toList());

            List<Long> recommendedReviewIds = recommendations.stream()
                    .map(rec -> Long.parseLong(rec.getId()))
                    .collect(Collectors.toList());

            if (recommendedReviewIds.isEmpty()) {
                return Mono.just(Collections.emptyList());
            }

            List<Review> reviews = reviewRepository.findAllById(recommendedReviewIds);

            List<ReviewResponse> reviewResponses = reviews.stream()
                    .map(review -> {
                        boolean isLiked = reviewLikeRepository.existsByUserAndReview(user, review);
                        return new ReviewResponse(review, isLiked);
                    })
                    .collect(Collectors.toList());

            return Mono.just(reviewResponses);

        } catch (ApiException e) {
            log.error("Recombee 추천 서버 호출 오류: {}", e.getMessage());
            return Mono.just(Collections.emptyList());
        }
    }
}