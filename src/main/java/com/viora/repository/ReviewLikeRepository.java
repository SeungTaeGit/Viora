package com.viora.repository;

import com.viora.entity.Review;
import com.viora.entity.ReviewLike;
import com.viora.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    boolean existsByUserAndReview(User user, Review review);
    Optional<ReviewLike> findByUserAndReview(User user, Review review);
}
