package com.viora.dto;

import com.viora.entity.User;
import lombok.Getter;

@Getter
public class MyProfileResponse {
    private final String email;
    private final String nickname;
    private final String profileImageUrl;
    private final String bio;
    private final String provider;

    public MyProfileResponse(User user) {
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.profileImageUrl = user.getProfileImageUrl();
        this.bio = user.getBio();
        this.provider = user.getProvider().toString(); // Enum을 문자열로 변환
    }
}