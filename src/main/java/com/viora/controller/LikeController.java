package com.viora.controller;

import com.viora.dto.UserSimpleDto;
import com.viora.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    // 좋아요 추가: POST /api/reviews/{reviewId}/likes
    @PostMapping("/{reviewId}/likes")
    public ResponseEntity<Void> addLike(@PathVariable Long reviewId,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        likeService.addLike(reviewId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    // 좋아요 취소: DELETE /api/reviews/{reviewId}/likes
    @DeleteMapping("/{reviewId}/likes")
    public ResponseEntity<Void> removeLike(@PathVariable Long reviewId,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        likeService.removeLike(reviewId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    /**
     * 특정 리뷰를 좋아한 사용자 목록 조회 API
     * GET /api/reviews/{reviewId}/likers
     */
    @GetMapping("/{reviewId}/likers")
    public ResponseEntity<List<UserSimpleDto>> getLikers(@PathVariable Long reviewId) {
        List<UserSimpleDto> likers = likeService.findLikersByReviewId(reviewId);
        return ResponseEntity.ok(likers);
    }
}