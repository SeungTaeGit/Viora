package com.viora.dto;

import com.viora.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MyCommentResponse {
    private final Long id;
    private final String text;
    private final Long reviewId; // 댓글이 달린 리뷰의 ID
    private final String reviewContentName; // 댓글이 달린 리뷰의 콘텐츠 이름
    private final LocalDateTime createdAt; // 댓글 작성 시간

    public MyCommentResponse(Comment comment) {
        this.id = comment.getId();
        this.text = comment.getText();
        this.reviewId = comment.getReview().getId(); // Comment 엔티티에서 Review 정보 가져오기
        this.reviewContentName = comment.getReview().getContentName(); // Comment 엔티티에서 Review 정보 가져오기
        this.createdAt = comment.getCreatedAt();
    }
}
