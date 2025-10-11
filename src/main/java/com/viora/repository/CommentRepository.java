package com.viora.repository;

import com.viora.entity.Comment;
import com.viora.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 리뷰(Review)에 달린 모든 댓글을 찾는 기능
    // SELECT * FROM comments WHERE review_id = ?
    List<Comment> findAllByReview(Review review);

}