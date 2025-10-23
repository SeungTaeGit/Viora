package com.viora.controller;

import com.viora.dto.CommentCreateRequest;
import com.viora.dto.CommentUpdateRequest;
import com.viora.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.viora.dto.MyCommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

import java.net.URI;
import java.nio.file.AccessDeniedException;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 생성: POST /api/reviews/{reviewId}/comments
    @PostMapping("/api/reviews/{reviewId}/comments")
    public ResponseEntity<Void> createComment(@PathVariable Long reviewId,
                                              @Valid @RequestBody CommentCreateRequest request,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        Long commentId = commentService.createComment(reviewId, request, userDetails.getUsername());
        return ResponseEntity.created(URI.create("/api/comments/" + commentId)).build();
    }

    // 댓글 수정: PUT /api/comments/{commentId}
    @PutMapping("/api/comments/{commentId}")
    public ResponseEntity<Void> updateComment(@PathVariable Long commentId,
                                              @Valid @RequestBody CommentUpdateRequest request,
                                              @AuthenticationPrincipal UserDetails userDetails) throws AccessDeniedException {
        commentService.updateComment(commentId, request, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    // 댓글 삭제: DELETE /api/comments/{commentId}
    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId,
                                              @AuthenticationPrincipal UserDetails userDetails) throws AccessDeniedException {
        commentService.deleteComment(commentId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    // 댓글 목록 페이징 조회를 위한 API 추가
    // GET /api/reviews/{reviewId}/comments
    @GetMapping("/api/reviews/{reviewId}/comments")
    public ResponseEntity<Page<MyCommentResponse>> getCommentsByReview(
            @PathVariable Long reviewId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<MyCommentResponse> comments = commentService.findCommentsByReview(reviewId, pageable);
        return ResponseEntity.ok(comments);
    }
}