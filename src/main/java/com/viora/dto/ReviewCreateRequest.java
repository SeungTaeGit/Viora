package com.viora.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewCreateRequest {
    private String category;
    private String contentName;
    private String location;
    private String text;
    private int rating;
}