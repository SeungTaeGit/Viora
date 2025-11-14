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
    private final int likeCount;
    private final boolean isLiked;
    private final List<CommentResponse> comments;
    private final String imageUrl;

    public ReviewResponse(Review review, boolean isLiked) {
        this.id = review.getId();
        this.authorNickname = review.getUser().getNickname();
        this.category = review.getCategory();
        this.contentName = review.getContentName();
        this.location = review.getLocation();
        this.text = review.getText();
        this.rating = review.getRating();
        this.createdAt = review.getCreatedAt();
        this.likeCount = review.getLikeCount();
        this.isLiked = isLiked;
        this.imageUrl = review.getImageUrl();
        this.comments = review.getComments().stream()
                .map(comment -> new CommentResponse(comment))
                .collect(Collectors.toList());
    }
}