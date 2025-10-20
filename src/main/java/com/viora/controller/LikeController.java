package com.viora.controller;

import com.viora.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews/{reviewId}/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    // 좋아요 추가: POST /api/reviews/{reviewId}/likes
    @PostMapping
    public ResponseEntity<Void> addLike(@PathVariable Long reviewId,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        likeService.addLike(reviewId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    // 좋아요 취소: DELETE /api/reviews/{reviewId}/likes
    @DeleteMapping
    public ResponseEntity<Void> removeLike(@PathVariable Long reviewId,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        likeService.removeLike(reviewId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}