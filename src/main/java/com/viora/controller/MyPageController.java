package com.viora.controller;

import com.viora.dto.MyCommentResponse;
import com.viora.dto.ReviewResponse;
import com.viora.service.CommentService;
import com.viora.service.LikeService;
import com.viora.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me") // "나"와 관련된 API들의 기본 경로
@RequiredArgsConstructor
public class MyPageController {

    private final ReviewService reviewService;
    private final CommentService commentService;
    private final LikeService likeService;

    /**
     * 내가 쓴 리뷰 목록 조회 API (페이지네이션 적용)
     */
    @GetMapping("/reviews")
    public ResponseEntity<Page<ReviewResponse>> getMyReviews(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 5) Pageable pageable) {

        Page<ReviewResponse> myReviews = reviewService.findMyReviews(userDetails.getUsername(), pageable);
        return ResponseEntity.ok(myReviews);
    }

    /**
     * 내가 쓴 댓글 목록 조회 API (페이지네이션 적용)
     */
    @GetMapping("/comments")
    public ResponseEntity<Page<MyCommentResponse>> getMyComments(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<MyCommentResponse> myComments = commentService.findMyComments(userDetails.getUsername(), pageable);
        return ResponseEntity.ok(myComments);
    }

    /**
     * 좋아요 한 리뷰 목록 조회 API (페이지네이션 적용)
     */
    @GetMapping("/liked-reviews")
    public ResponseEntity<Page<ReviewResponse>> getMyLikedReviews(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 5) Pageable pageable) {

        Page<ReviewResponse> myLikedReviews = likeService.findMyLikedReviews(userDetails.getUsername(), pageable);
        return ResponseEntity.ok(myLikedReviews);
    }
}