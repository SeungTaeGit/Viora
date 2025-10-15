package com.viora.dto;

import com.viora.entity.Review;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ReviewResponse {
    private final Long id;
    private final String authorNickname;
    private final String category;
    private final String contentName;
    private final String location;
    private final String text;
    private final int rating;
    private final LocalDateTime createdAt;
    private final List<CommentResponse> comments; // ❗️ 댓글 목록 필드 추가

    public ReviewResponse(Review review) {
        this.id = review.getId();
        this.authorNickname = review.getUser().getNickname();
        this.category = review.getCategory();
        this.contentName = review.getContentName();
        this.location = review.getLocation();
        this.text = review.getText();
        this.rating = review.getRating();
        this.createdAt = review.getCreatedAt();

        // ❗️ Review Entity에 있는 Comment 리스트를 CommentResponse 리스트로 변환
        this.comments = review.getComments().stream()
                .map(CommentResponse::new)
                .collect(Collectors.toList());
    }
}