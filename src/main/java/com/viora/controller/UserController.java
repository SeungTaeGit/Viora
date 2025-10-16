package com.viora.controller;

import com.viora.dto.MyProfileResponse;
import com.viora.dto.ProfileUpdateRequest;
import com.viora.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 내 프로필 정보 조회 API
    @GetMapping("/me")
    public ResponseEntity<MyProfileResponse> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        MyProfileResponse profile = userService.getMyProfile(userDetails.getUsername());
        return ResponseEntity.ok(profile);
    }

    // 내 프로필 정보 수정 API
    @PutMapping("/me")
    public ResponseEntity<Void> updateMyProfile(@AuthenticationPrincipal UserDetails userDetails,
                                                @Valid @RequestBody ProfileUpdateRequest request) {
        userService.updateMyProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok().build();
    }
}