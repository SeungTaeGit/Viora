package com.viora.entity;

import com.viora.entity.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "reviews")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 리뷰 작성자

    @Column(nullable = false)
    private String category; // 예: 맛집, 숙소, 영화

    @Column(nullable = false)
    private String contentName; // 예: 스타벅스 경복궁점

    private String location; // 위치 정보 (선택)

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text; // 리뷰 본문

    @Column(nullable = false)
    private int rating; // 별점

    @Column(nullable = false)
    @ColumnDefault("0") // DB에 기본값을 0으로 설정
    private int likeCount;

    @Column(length = 500)
    private String imageUrl;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewLike> reviewLikes = new ArrayList<>();

    @Builder
    public Review(User user, String category, String contentName, String location, String text, int rating, String imageUrl) {
        this.user = user;
        this.category = category;
        this.contentName = contentName;
        this.location = location;
        this.text = text;
        this.rating = rating;
        this.imageUrl = imageUrl; // Builder에도 추가
    }

    // 리뷰 수정 메서드 (imageUrl 추가)
    public void update(String category, String contentName, String location, String text, int rating, String imageUrl) {
        this.category = category;
        this.contentName = contentName;
        this.location = location;
        this.text = text;
        this.rating = rating;
        this.imageUrl = imageUrl;
    }

    // 좋아요 카운트 메서드
    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        this.likeCount--;
    }
}