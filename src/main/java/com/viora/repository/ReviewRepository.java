package com.viora.repository;

import com.viora.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.viora.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Spring Data JPA의 쿼리 메서드 기능
    // 카테고리(맛집, 숙소 등)별로 리뷰를 찾는 기능
    // SELECT * FROM reviews WHERE category = ? ORDER BY created_at DESC
    List<Review> findByCategoryOrderByCreatedAtDesc(String category);

    // 특정 User가 작성한 Review들을 페이지 단위로 조회 (최신순으로 정렬)
    Page<Review> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}
