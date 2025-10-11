package com.viora.entity;

import com.viora.entity.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    // '리뷰(N)'는 '하나의 사용자(1)'에게 속합니다.
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

    // '하나의 리뷰(1)'는 '여러 개의 댓글(N)'을 가질 수 있습니다.
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Review(User user, String category, String contentName, String location, String text, int rating) {
        this.user = user;
        this.category = category;
        this.contentName = contentName;
        this.location = location;
        this.text = text;
        this.rating = rating;
    }

    public void update(String category, String contentName, String location, String text, int rating) {
        this.category = category;
        this.contentName = contentName;
        this.location = location;
        this.text = text;
        this.rating = rating;
    }
    /* 'update()' 메서드가 Service가 아닌 Entity에 있는 이유.

    객체지향 프로그래밍(OOP)의 핵심 철학은 "데이터와 그 데이터를 처리하는 행동(메서드)은 한곳에 모여 있어야 한다"는 것입니다. 객체는 자신의 상태를 스스로 책임져야 합니다.

    책임의 명확성: "리뷰의 데이터를 수정하는 책임"은 ReviewService가 아니라 Review 객체 자신에게 있는 것이 자연스럽습니다. Review가 자신의 데이터를 가장 잘 알기 때문이죠.

    코드 응집도 증가: 리뷰를 수정하는 모든 로직이 Review 클래스 안의 update() 메서드 하나로 모입니다. 만약 나중에 "리뷰를 수정하면 수정 횟수도 1 증가시켜야 한다"는 규칙이 추가되면, Review의 update() 메서드만 수정하면 됩니다. 첫 번째 방식이었다면, 리뷰를 수정하는 모든 서비스 코드를 찾아다니며 수정해야 했을 겁니다.

    서비스 로직 단순화: ReviewService는 "어떤 리뷰를 찾을지", "권한이 있는지"만 신경 쓰고, 실제 데이터 수정은 Review 객체에게 위임하면 되므로 코드가 훨씬 깔끔하고 단순해집니다.

    */

}
