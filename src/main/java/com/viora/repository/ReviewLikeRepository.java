package com.viora.repository;

import com.viora.entity.Review;
import com.viora.entity.ReviewLike;
import com.viora.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    boolean existsByUserAndReview(User user, Review review);
    Optional<ReviewLike> findByUserAndReview(User user, Review review);

    // 특정 User의 '좋아요' 기록을 페이지 단위로 조회
    Page<ReviewLike> findByUser(User user, Pageable pageable);
}
