package com.viora.repository;

import com.viora.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Spring Data JPA의 쿼리 메서드 기능
    // 카테고리(맛집, 숙소 등)별로 리뷰를 찾는 기능
    // SELECT * FROM reviews WHERE category = ? ORDER BY created_at DESC
    List<Review> findByCategoryOrderByCreatedAtDesc(String category);

}
