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
    @Transactional(readOnly = true) // 조회 기능은 readOnly=true로 성능 최적화
    public List<ReviewResponse> findAllReviews() {
        return reviewRepository.findAll().stream()
                .map(ReviewResponse::new) // review -> new ReviewResponse(review)
                .collect(Collectors.toList());
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

    /**
     * 리뷰 수정
     */
    public void updateReview(Long reviewId, ReviewUpdateRequest request, String userEmail) throws AccessDeniedException {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        // 🔐 권한 검증 (이 책임은 Service에 있는 것이 맞습니다)
        if (!review.getUser().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("리뷰를 수정할 권한이 없습니다.");
        }

        // 👍 Entity에게 데이터 수정을 위임! Service는 명령만 내립니다.
        review.update(
                request.getCategory(),
                request.getContentName(),
                request.getLocation(),
                request.getText(),
                request.getRating()
        );
    }

    /**
     * 리뷰 삭제
     */
    public void deleteReview(Long reviewId, String userEmail) throws AccessDeniedException {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        // 🔐 권한 검증: 현재 로그인한 사용자가 리뷰 작성자인지 확인
        if (!review.getUser().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("리뷰를 삭제할 권한이 없습니다.");
        }

        reviewRepository.delete(review);
    }
}