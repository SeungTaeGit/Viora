package com.viora.dto;

import com.viora.entity.Review;
import lombok.Getter;

import java.time.LocalDateTime;

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
    // 댓글 목록도 나중에 추가 예정

    public ReviewResponse(Review review) {
        this.id = review.getId();
        this.authorNickname = review.getUser().getNickname();
        this.category = review.getCategory();
        this.contentName = review.getContentName();
        this.location = review.getLocation();
        this.text = review.getText();
        this.rating = review.getRating();
        this.createdAt = review.getCreatedAt();
    }
}