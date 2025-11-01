package com.viora.repository;

import com.viora.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.viora.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.viora.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Spring Data JPA의 쿼리 메서드 기능
    // 카테고리(맛집, 숙소 등)별로 리뷰를 찾는 기능
    // SELECT * FROM reviews WHERE category = ? ORDER BY created_at DESC
    List<Review> findByCategoryOrderByCreatedAtDesc(String category);

    // ❗️ '좋아요' 많은 순으로 리뷰를 찾는 기능 추가
    Page<Review> findAllByOrderByLikeCountDesc(Pageable pageable);

    // 특정 User가 작성한 Review들을 페이지 단위로 조회 (최신순으로 정렬)
    Page<Review> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    /**
     * '콘텐츠 이름'에 검색어가 포함된 리뷰를 페이지 단위로 조회
     */
    Page<Review> findByContentNameContaining(String keyword, Pageable pageable);

    /**
     * '리뷰 본문(text)'에 검색어가 포함된 리뷰를 페이지 단위로 조회
     */
    Page<Review> findByTextContaining(String keyword, Pageable pageable);

    /**
     * '카테고리'가 검색어와 일치하는 리뷰를 페이지 단위로 조회 (선택 방식이므로 Containing 불필요)
     */
    Page<Review> findByCategory(String category, Pageable pageable);

    /**
     * '작성자(User)의 닉네임'에 검색어가 포함된 리뷰를 페이지 단위로 조회
     * User 엔티티의 nickname 필드를 검색합니다.
     */
    Page<Review> findByUser_NicknameContaining(String nickname, Pageable pageable);
}
