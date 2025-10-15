package com.viora.entity;

import com.viora.entity.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "comments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // '댓글(N)'은 '하나의 사용자(1)'에게 속합니다. (댓글 작성자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // '댓글(N)'은 '하나의 리뷰(1)'에 속합니다. (댓글이 달린 리뷰)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Column(nullable = false)
    private String text; // 댓글 내용

    @Builder
    public Comment(User user, Review review, String text) {
        this.user = user;
        this.review = review;
        this.text = text;
    }

    public void update(String text) {
        this.text = text;
    }
}