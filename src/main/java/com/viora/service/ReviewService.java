package com.viora.service;

import com.viora.dto.ReviewCreateRequest;
import com.viora.dto.ReviewResponse;
import com.viora.dto.ReviewUpdateRequest;
import com.viora.entity.Review;
import com.viora.entity.User;
import com.viora.repository.ReviewLikeRepository;
import com.viora.repository.ReviewRepository;
import com.viora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional // 클래스 레벨에 선언하면 모든 public 메서드에 트랜잭션이 적용됩니다.
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewLikeRepository reviewLikeRepository;

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
        Page<Review> reviewPage = reviewRepository.findAll(pageable);

        return reviewPage.map(review -> new ReviewResponse(review, false));
    }

    /**
     * 리뷰 단건 조회 (isLiked 로직 추가)
     */
    @Transactional(readOnly = true)
    public ReviewResponse findReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isLiked = false;

        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            String userEmail = authentication.getName();
            User user = userRepository.findByEmail(userEmail)
                    .orElse(null); // 사용자가 없을 수도 있으므로 orElse(null) 처리

            if (user != null) {
                isLiked = reviewLikeRepository.existsByUserAndReview(user, review);
            }
        }

        return new ReviewResponse(review, isLiked);
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
     * 내가 쓴 리뷰 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<ReviewResponse> findMyReviews(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Page<Review> reviewPage = reviewRepository.findByUserOrderByCreatedAtDesc(user, pageable);

        return reviewPage.map(review -> new ReviewResponse(review, false));
    }
}