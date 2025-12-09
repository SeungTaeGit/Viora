package com.viora.dto;

import com.viora.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MyCommentResponse {
    private final Long id;
    private final String text;
    private final Long reviewId;
    private final String reviewContentName;
    private final LocalDateTime createdAt;
    private final String authorNickname;

    public MyCommentResponse(Comment comment) {
        this.id = comment.getId();
        this.text = comment.getText();
        this.reviewId = comment.getReview().getId();
        this.reviewContentName = comment.getReview().getContentName();
        this.createdAt = comment.getCreatedAt();
        this.authorNickname = comment.getUser().getNickname();
    }
}
