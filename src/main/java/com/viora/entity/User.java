package com.viora.entity;

// src/main/java/com/viora/entity/User.java

import com.viora.entity.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "users") // DB 테이블 이름을 'users'로 지정합니다. 'user'는 예약어인 경우가 많아 피하는 것이 좋습니다.
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA는 기본 생성자를 필요로 합니다. 보안을 위해 protected로 설정합니다.
@Where(clause = "deleted_at IS NULL") // SELECT 쿼리를 날릴 때 항상 deleted_at이 NULL인 데이터만 조회하도록 필터링합니다.
@SQLDelete(sql = "UPDATE users SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?") // delete 요청 시 실제 삭제 대신 deleted_at에 현재 시간을 기록합니다.
public class User extends BaseTimeEntity { // BaseTimeEntity를 상속받아 createdAt, updatedAt 칼럼을 자동으로 가집니다.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB가 ID를 자동으로 생성하고 관리하도록 합니다. (MySQL의 AUTO_INCREMENT)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING) // Enum 타입을 DB에 저장할 때 문자열 그대로 저장합니다. (예: "GOOGLE")
    @Column(nullable = false)
    private Provider provider;

    @Column
    private LocalDateTime deletedAt; // Soft Delete를 위한 칼럼

    // '하나의 사용자(1)'는 '여러 개의 리뷰(N)'를 가질 수 있습니다.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    // '하나의 사용자(1)'는 '여러 개의 댓글(N)'을 가질 수 있습니다.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @Column(length = 500) // URL 길이를 고려하여 넉넉하게 설정
    private String profileImageUrl;

    @Column(length = 100)
    private String bio; // 한 줄 소개 (bio)

    // 빌더 패턴: 객체를 생성할 때 가독성 좋고 안전하게 만들 수 있습니다.
    @Builder
    public User(String email, String passwordHash, String nickname, Provider provider) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.nickname = nickname;
        this.provider = provider;
    }

    public void updateProfile(String nickname, String profileImageUrl, String bio) {

        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.bio = bio;
    }
}