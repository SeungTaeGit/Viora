package com.viora.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewUpdateRequest {
    @NotBlank(message = "카테고리는 필수 입력 항목입니다.") // 비어있거나 공백일 수 없음
    private String category;

    @NotBlank(message = "콘텐츠 이름은 필수 입력 항목입니다.")
    @Size(max = 100, message = "콘텐츠 이름은 100자를 초과할 수 없습니다.") // 문자열 길이 제한
    private String contentName;

    private String location; // location은 선택 항목이므로 검증 X

    @NotBlank(message = "리뷰 내용은 필수 입력 항목입니다.")
    @Size(max = 2000, message = "리뷰 내용은 2000자를 초과할 수 없습니다.")
    private String text;

    @NotNull(message = "별점은 필수 입력 항목입니다.") // Null일 수 없음
    @Min(value = 1, message = "별점은 1점 이상이어야 합니다.") // 최솟값
    @Max(value = 5, message = "별점은 5점 이하이어야 합니다.") // 최댓값
    private int rating;

    private String imageUrl;
}