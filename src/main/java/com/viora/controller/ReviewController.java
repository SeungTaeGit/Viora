package com.viora.controller;

import com.viora.dto.ReviewCreateRequest;
import com.viora.dto.ReviewResponse;
import com.viora.dto.ReviewUpdateRequest;
import com.viora.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.net.URI;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/reviews") // 이 컨트롤러의 모든 API는 /api/reviews 로 시작합니다.
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰 생성 API
     */
    @PostMapping
    public ResponseEntity<Void> createReview(@Valid @RequestBody ReviewCreateRequest request,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        // 🔐 로그인한 사용자의 이메일 정보를 Service에 함께 넘겨줍니다.
        Long reviewId = reviewService.createReview(request, userDetails.getUsername());
        return ResponseEntity.created(URI.create("/api/reviews/" + reviewId)).build();
    }

    /**
     * 리뷰 전체 조회 API
     */
    @GetMapping
    public ResponseEntity<Page<ReviewResponse>> getAllReviews(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ReviewResponse> reviews = reviewService.findAllReviews(pageable);
        return ResponseEntity.ok(reviews);
    }

    /**
     * 리뷰 단건 조회 API
     */
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable Long reviewId) {
        ReviewResponse review = reviewService.findReviewById(reviewId);
        return ResponseEntity.ok(review);
    }

    /**
     * 리뷰 수정 API
     */
    @PutMapping("/{reviewId}")
    public ResponseEntity<Void> updateReview(@PathVariable Long reviewId,
                                             @Valid @RequestBody ReviewUpdateRequest request,
                                             @AuthenticationPrincipal UserDetails userDetails) throws AccessDeniedException {
        // 🔐 수정 권한 확인을 위해 로그인한 사용자 정보를 Service에 넘겨줍니다.
        reviewService.updateReview(reviewId, request, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    /**
     * 리뷰 삭제 API
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId,
                                             @AuthenticationPrincipal UserDetails userDetails) throws AccessDeniedException {
        // 🔐 삭제 권한 확인을 위해 로그인한 사용자 정보를 Service에 넘겨줍니다.
        reviewService.deleteReview(reviewId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}