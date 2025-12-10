package com.viora.service;

import com.recombee.api_client.RecombeeClient;
import com.recombee.api_client.api_requests.AddBookmark;
import com.recombee.api_client.api_requests.DeleteBookmark;
import com.recombee.api_client.exceptions.ApiException;
import com.recombee.api_client.RecombeeClient;
import com.viora.dto.UserSimpleDto;
import com.viora.entity.Review;
import com.viora.entity.ReviewLike;
import com.viora.entity.User;
import com.viora.repository.ReviewLikeRepository;
import com.viora.repository.ReviewRepository;
import com.viora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.viora.dto.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;              // List import
import java.util.stream.Collectors; // Collectors import

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final ReviewLikeRepository reviewLikeRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final RecombeeClient recombeeClient;

    /**
     * 좋아요 추가 (Recombee 동기화)
     */
    public void addLike(Long reviewId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        if (reviewLikeRepository.existsByUserAndReview(user, review)) {
            throw new IllegalArgumentException("이미 좋아요를 누른 리뷰입니다.");
        }

        ReviewLike reviewLike = ReviewLike.builder()
                .user(user)
                .review(review)
                .build();
        reviewLikeRepository.save(reviewLike);

        review.incrementLikeCount();

        try {
            String userIdStr = user.getId().toString();
            String reviewIdStr = review.getId().toString();

            recombeeClient.send(new AddBookmark(userIdStr, reviewIdStr)
                    .setCascadeCreate(true)
                    .setTimestamp(new Date())
            );
            log.info("Recombee 'AddBookmark' 동기화 성공: User ID {}, Review ID {}", userIdStr, reviewIdStr);
        } catch (ApiException e) {
            log.error("Recombee 'AddBookmark' 동기화 실패: {}", e.getMessage());
        }
    }

    /**
     * 좋아요 취소 (Recombee 동기화)
     */
    public void removeLike(Long reviewId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        ReviewLike reviewLike = reviewLikeRepository.findByUserAndReview(user, review)
                .orElseThrow(() -> new IllegalArgumentException("좋아요 기록을 찾을 수 없습니다."));

        reviewLikeRepository.delete(reviewLike);
        review.decrementLikeCount();

        try {
            String userIdStr = user.getId().toString();
            String reviewIdStr = review.getId().toString();

            recombeeClient.send(new DeleteBookmark(userIdStr, reviewIdStr)
                    .setTimestamp(new Date())
            );
            log.info("Recombee 'DeleteBookmark' 동기화 성공: User ID {}, Review ID {}", userIdStr, reviewIdStr);
        } catch (ApiException e) {
            log.error("Recombee 'DeleteBookmark' 동기화 실패: {}", e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> findMyLikedReviews(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Page<ReviewLike> likedPage = reviewLikeRepository.findByUser(user, pageable);

        return likedPage.map(reviewLike -> new ReviewResponse(reviewLike.getReview(), true));
    }

    /**
     * 특정 리뷰를 좋아한 사용자 목록 조회
     */
    @Transactional(readOnly = true)
    public List<UserSimpleDto> findLikersByReviewId(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        List<ReviewLike> likes = reviewLikeRepository.findByReview(review);

        return likes.stream()
                .map(like -> new UserSimpleDto(like.getUser()))
                .collect(Collectors.toList());
    }
}