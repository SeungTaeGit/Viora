package com.viora.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter; // Setter 추가
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RecommendationResponse {
    private List<Long> recommendedReviewIds;
}

