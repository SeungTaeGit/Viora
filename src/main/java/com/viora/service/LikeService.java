package com.viora.service;

import com.viora.entity.Review;
import com.viora.entity.ReviewLike;
import com.viora.entity.User;
import com.viora.repository.ReviewLikeRepository;
import com.viora.repository.ReviewRepository;
import com.viora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.viora.dto.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final ReviewLikeRepository reviewLikeRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    public void addLike(Long reviewId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        // 이미 좋아요를 눌렀는지 확인
        if (reviewLikeRepository.existsByUserAndReview(user, review)) {
            throw new IllegalArgumentException("이미 좋아요를 누른 리뷰입니다.");
        }

        // 1. '좋아요' 기록 생성 및 저장
        ReviewLike reviewLike = ReviewLike.builder()
                .user(user)
                .review(review)
                .build();
        reviewLikeRepository.save(reviewLike);

        // 2. 리뷰의 좋아요 카운트 1 증가
        review.incrementLikeCount();
    }

    public void removeLike(Long reviewId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        // 1. 삭제할 '좋아요' 기록 찾기
        ReviewLike reviewLike = reviewLikeRepository.findByUserAndReview(user, review)
                .orElseThrow(() -> new IllegalArgumentException("좋아요 기록을 찾을 수 없습니다."));

        // 2. '좋아요' 기록 삭제
        reviewLikeRepository.delete(reviewLike);

        // 3. 리뷰의 좋아요 카운트 1 감소
        review.decrementLikeCount();
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> findMyLikedReviews(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Page<ReviewLike> likedPage = reviewLikeRepository.findByUser(user, pageable);

        // ❗️ reviewLike.getReview()와 함께 'true'를 두 번째 인자로 넘겨줍니다.
        return likedPage.map(reviewLike -> new ReviewResponse(reviewLike.getReview(), true));
    }
}