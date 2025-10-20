package com.viora.dto; // 패키지 경로는 실제 프로젝트에 맞게 확인해주세요.

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
    private final boolean isLiked; // 1. ❗️ isLiked 필드를 추가합니다.
    private final List<CommentResponse> comments;

    // 2. ❗️ 생성자에서 boolean isLiked 파라미터를 받도록 수정합니다. ❗️
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
        this.isLiked = isLiked; // 3. ❗️ 전달받은 isLiked 값으로 필드를 초기화합니다.
        this.comments = review.getComments().stream()
                .map(comment -> new CommentResponse(comment)) // CommentResponse 생성자에 맞게 수정
                .collect(Collectors.toList());
    }
}