package com.viora.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Getter
@RequiredArgsConstructor // 간단하게 생성자를 통해 값을 받도록 설정
public class RecommendationRequest {
    private final Long userId; // 사용자 ID
    private final List<Long> likedReviewIds; // 최근 좋아요 누른 리뷰 ID 목록

    // 필요에 따라 다른 맥락 정보 필드 추가
}
