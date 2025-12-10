package com.viora.service;

import com.recombee.api_client.RecombeeClient;
import com.recombee.api_client.api_requests.AddRating;
import com.recombee.api_client.api_requests.SetItemValues;
import com.recombee.api_client.exceptions.ApiException;
import com.recombee.api_client.api_requests.AddDetailView;
import com.viora.dto.ReviewCreateRequest;
import com.viora.dto.ReviewResponse;
import com.viora.dto.ReviewUpdateRequest;
import com.viora.entity.Review;
import com.viora.entity.User;
import com.viora.repository.ReviewLikeRepository;
import com.viora.repository.ReviewRepository;
import com.viora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional // 클래스 레벨에 선언하면 모든 public 메서드에 트랜잭션이 적용됩니다.
public class ReviewService {

    private final RecombeeClient recombeeClient;
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
                .imageUrl(request.getImageUrl())
                .build();

        Review savedReview = reviewRepository.save(review);
        Long reviewId = savedReview.getId();
        Long userId = user.getId();

        try {
            Map<String, Object> itemValues = new HashMap<>();
            itemValues.put("category", review.getCategory());
            itemValues.put("contentName", review.getContentName());
            itemValues.put("location", review.getLocation());
            recombeeClient.send(new SetItemValues(reviewId.toString(), itemValues)
                    .setCascadeCreate(true));

            double normalizedRating = (review.getRating() - 3.0) / 2.0;
            recombeeClient.send(new AddRating(userId.toString(), reviewId.toString(), normalizedRating)
                    .setCascadeCreate(true)
                    .setTimestamp(new Date(System.currentTimeMillis())));

            log.info("Recombee 데이터 동기화 성공: Review ID {}, User ID {}", reviewId, userId);

        } catch (ApiException e) {
            log.error("Recombee 데이터 동기화 실패: {}", e.getMessage());
        }

        return reviewId;
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
     * 리뷰 단건 조회 (Recombee 동기화)
     */
    @Transactional(readOnly = true)
    public ReviewResponse findReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isLiked = false;
        User user = null;

        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            String userEmail = authentication.getName();
            user = userRepository.findByEmail(userEmail).orElse(null);

            if (user != null) {
                isLiked = reviewLikeRepository.existsByUserAndReview(user, review);

                try {
                    String userIdStr = user.getId().toString();
                    String reviewIdStr = review.getId().toString();

                    recombeeClient.send(new AddDetailView(userIdStr, reviewIdStr)
                            .setCascadeCreate(true)
                            .setTimestamp(new Date())
                    );
                    log.debug("Recombee 'AddDetailView' 동기화 성공: User ID {}, Review ID {}", userIdStr, reviewIdStr);
                } catch (ApiException e) {
                    log.warn("Recombee 'AddDetailView' 동기화 실패: {}", e.getMessage());
                }
            }
        }

        return new ReviewResponse(review, isLiked);
    }

    public void updateReview(Long reviewId, ReviewUpdateRequest request, String userEmail) throws AccessDeniedException {
        Review review = findReviewAndCheckOwnership(reviewId, userEmail);

        review.update(
                request.getCategory(),
                request.getContentName(),
                request.getLocation(),
                request.getText(),
                request.getRating(),
                request.getImageUrl()
        );
    }

    public void deleteReview(Long reviewId, String userEmail) throws AccessDeniedException {
        Review review = findReviewAndCheckOwnership(reviewId, userEmail);

        reviewRepository.delete(review);
    }

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

    /**
     * 인기 리뷰 목록 조회 (좋아요 순, 페이지네이션)
     */
    @Transactional(readOnly = true)
    public Page<ReviewResponse> findPopularReviews(Pageable pageable) {
        Page<Review> reviewPage = reviewRepository.findAllByOrderByLikeCountDesc(pageable);

        // ❗️ isLiked 계산 로직 추가 필요 (또는 기본값 false 사용)
        // 이 목록은 특정 사용자를 위한 것이 아니므로, isLiked는 false로 설정하는 것이 간단합니다.
        return reviewPage.map(review -> new ReviewResponse(review, false));
    }

    /**
     * 검색 유형과 키워드를 받아 리뷰를 검색하는 메서드
     */
    public Page<ReviewResponse> searchReviews(String searchType, String keyword, Pageable pageable) {
        Page<Review> reviewPage;

        // 검색 유형(searchType)에 따라 분기 처리
        switch (searchType) {
            case "contentName":
                reviewPage = reviewRepository.findByContentNameContaining(keyword, pageable);
                break;
            case "text":
                reviewPage = reviewRepository.findByTextContaining(keyword, pageable);
                break;
            case "category":
                reviewPage = reviewRepository.findByCategory(keyword, pageable);
                break;
            case "author":
                reviewPage = reviewRepository.findByUser_NicknameContaining(keyword, pageable);
                break;
            default:
                // 기본값 또는 예외 처리 (여기서는 기본적으로 전체 목록 반환)
                log.warn("알 수 없는 검색 유형입니다: {}", searchType);
                reviewPage = reviewRepository.findAll(pageable);
                break;
        }

        // 검색 결과(Page<Review>)를 Page<ReviewResponse>로 변환
        // ❗️ 로그인 상태에 따라 'isLiked'를 다르게 설정해야 함
        // (간결함을 위해 여기서는 'false'로 통일, 추후 개선 가능)
        return reviewPage.map(review -> new ReviewResponse(review, false));
    }
}