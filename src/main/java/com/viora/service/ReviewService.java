package com.viora.service;

import com.viora.dto.ReviewCreateRequest;
import com.viora.dto.ReviewResponse;
import com.viora.dto.ReviewUpdateRequest;
import com.viora.entity.Review;
import com.viora.entity.User;
import com.viora.repository.ReviewRepository;
import com.viora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional // 클래스 레벨에 선언하면 모든 public 메서드에 트랜잭션이 적용됩니다.
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    /**
     * 리뷰 생성
     */
    public Long createReview(ReviewCreateRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Review review = Review.builder()
                .user(user)
                .category(request.getCategory())
                .contentName(request.getContentName())
                .location(request.getLocation())
                .text(request.getText())
                .rating(request.getRating())
                .build();

        Review savedReview = reviewRepository.save(review);
        return savedReview.getId();
    }

    /**
     * 리뷰 전체 조회
     */
    @Transactional(readOnly = true)
    public Page<ReviewResponse> findAllReviews(Pageable pageable) {
        // 1. Repository로부터 Page<Review>를 받음
        Page<Review> reviewPage = reviewRepository.findAll(pageable);

        // 2. Page<Review>를 Page<ReviewResponse>로 변환
        return reviewPage.map(ReviewResponse::new);
    }

    /**
     * 리뷰 단건 조회
     */
    @Transactional(readOnly = true)
    public ReviewResponse findReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
        return new ReviewResponse(review);
    }

    public void updateReview(Long reviewId, ReviewUpdateRequest request, String userEmail) throws AccessDeniedException {
        // 1. 리뷰 찾기와 권한 검증을 private 메서드에 위임
        Review review = findReviewAndCheckOwnership(reviewId, userEmail);

        review.update(
                request.getCategory(),
                request.getContentName(),
                request.getLocation(),
                request.getText(),
                request.getRating()
        );
    }

    public void deleteReview(Long reviewId, String userEmail) throws AccessDeniedException {
        // 1. 리뷰 찾기와 권한 검증을 private 메서드에 위임
        Review review = findReviewAndCheckOwnership(reviewId, userEmail);

        reviewRepository.delete(review);
    }

    // 2. 중복 로직을 처리할 private 헬퍼 메서드 추가
    private Review findReviewAndCheckOwnership(Long reviewId, String userEmail) throws AccessDeniedException {
        // 리뷰 찾기
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        // 권한 검증
        if (!review.getUser().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("해당 리뷰에 대한 권한이 없습니다.");
        }

        return review;
    }

    /**
     * 내가 쓴 리뷰 목록 조회 (페이지네이션)
     */
    @Transactional(readOnly = true)
    public Page<ReviewResponse> findMyReviews(String userEmail, Pageable pageable) {
        // 1. 이메일로 User 엔티티를 검색.
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. Repository에 User 객체와 Pageable 객체를 넘겨 리뷰 페이지 리턴.
        Page<Review> reviewPage = reviewRepository.findByUserOrderByCreatedAtDesc(user, pageable);

        // 3. Page<Review>를 Page<ReviewResponse>로 변환하여 반환.
        return reviewPage.map(ReviewResponse::new);
    }
}