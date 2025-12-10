package com.viora.controller;

import com.recombee.api_client.RecombeeClient;
import com.recombee.api_client.api_requests.AddBookmark;
import com.recombee.api_client.api_requests.AddRating;
import com.recombee.api_client.api_requests.SetItemValues;
import com.recombee.api_client.api_requests.SetUserValues;
import com.recombee.api_client.api_requests.Batch;
import com.recombee.api_client.api_requests.Request;
import com.recombee.api_client.exceptions.ApiException;
import com.viora.entity.Review;
import com.viora.entity.ReviewLike;
import com.viora.entity.User;
import com.viora.repository.ReviewLikeRepository;
import com.viora.repository.ReviewRepository;
import com.viora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
public class SyncController {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final RecombeeClient recombeeClient;

    /**
     * Viora DB의 모든 데이터를 Recombee로 동기화하는 API
     */
    @PostMapping("/recombee")
    public ResponseEntity<String> syncToRecombee() {
        try {
            List<Request> requests = new ArrayList<>();

            List<User> users = userRepository.findAll();
            for (User user : users) {
                Map<String, Object> userValues = new HashMap<>();
                userValues.put("nickname", user.getNickname());
                requests.add(new SetUserValues(user.getId().toString(), userValues).setCascadeCreate(true));
            }
            log.info("사용자 {}명 동기화 준비 완료", users.size());

            List<Review> reviews = reviewRepository.findAll();
            for (Review review : reviews) {
                Map<String, Object> itemValues = new HashMap<>();
                itemValues.put("category", review.getCategory());
                itemValues.put("contentName", review.getContentName());
                itemValues.put("location", review.getLocation());
                requests.add(new SetItemValues(review.getId().toString(), itemValues).setCascadeCreate(true));

                double normalizedRating = (review.getRating() - 3.0) / 2.0;
                requests.add(new AddRating(review.getUser().getId().toString(), review.getId().toString(), normalizedRating)
                        .setTimestamp(java.sql.Timestamp.valueOf(review.getCreatedAt()))
                        .setCascadeCreate(true));
            }
            log.info("리뷰 {}개 동기화 준비 완료", reviews.size());

            List<ReviewLike> likes = reviewLikeRepository.findAll();
            for (ReviewLike like : likes) {
                requests.add(new AddBookmark(like.getUser().getId().toString(), like.getReview().getId().toString())
                                .setCascadeCreate(true)
                );
            }
            log.info("좋아요 {}개 동기화 준비 완료", likes.size());

            if (!requests.isEmpty()) {
                int batchSize = 1000;
                for (int i = 0; i < requests.size(); i += batchSize) {
                    int end = Math.min(requests.size(), i + batchSize);
                    List<Request> batchRequests = requests.subList(i, end);
                    recombeeClient.send(new Batch(batchRequests));
                    log.info("Batch 전송 완료: {} ~ {}", i, end);
                }
            }

            return ResponseEntity.ok("Recombee 동기화 완료: 총 " + requests.size() + "건 전송됨");

        } catch (ApiException e) {
            log.error("Recombee 동기화 실패: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("동기화 실패: " + e.getMessage());
        }
    }
}