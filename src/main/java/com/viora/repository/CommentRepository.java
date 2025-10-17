package com.viora.repository;

import com.viora.entity.Comment;
import com.viora.entity.Review;
import com.viora.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 리뷰(Review)에 달린 모든 댓글을 찾는 기능
    // SELECT * FROM comments WHERE review_id = ?
    List<Comment> findAllByReview(Review review);

    // 페이지네이션을 위한 메서드 추가
    // Review Entity의 id 필드를 기준으로 Comment를 찾는다는 의미 (findBy + Review_Id)
    // Spring Data JPA가 메서드 이름을 보고 SQL을 자동으로 생성
    Page<Comment> findByReview_Id(Long reviewId, Pageable pageable);

    // 특정 User가 작성한 Comment들을 페이지 단위로 조회 (최신순으로)
    Page<Comment> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}