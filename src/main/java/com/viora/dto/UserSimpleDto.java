package com.viora.dto;

import com.viora.entity.User;
import lombok.Getter;

@Getter
public class UserSimpleDto {
    private final String nickname;
    // 나중에 필요하면 프로필 이미지 URL 추가
    // private final String profileImageUrl;

    public UserSimpleDto(User user) {
        this.nickname = user.getNickname();
        // this.profileImageUrl = user.getProfileImageUrl();
    }
}