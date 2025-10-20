package com.viora.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfileUpdateRequest {

    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해주세요.")
    private String nickname;

    private String profileImageUrl;

    @Size(max = 100, message = "한 줄 소개는 100자를 초과할 수 없습니다.")
    private String bio;
}