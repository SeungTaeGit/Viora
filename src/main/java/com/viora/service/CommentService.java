package com.viora.service;

import com.viora.dto.CommentCreateRequest;
import com.viora.dto.CommentUpdateRequest;
import com.viora.entity.Comment;
import com.viora.entity.Review;
import com.viora.entity.User;
import com.viora.repository.CommentRepository;
import com.viora.repository.ReviewRepository;
import com.viora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    public Long createComment(Long reviewId, CommentCreateRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 작성할 리뷰를 찾을 수 없습니다."));

        Comment comment = Comment.builder()
                .user(user)
                .review(review)
                .text(request.getText())
                .build();

        Comment savedComment = commentRepository.save(comment);
        return savedComment.getId();
    }

    public void updateComment(Long commentId, CommentUpdateRequest request, String userEmail) throws AccessDeniedException {
        Comment comment = findCommentAndCheckOwnership(commentId, userEmail);
        comment.update(request.getText()); // Comment Entity에 update 메서드 추가 필요
    }

    public void deleteComment(Long commentId, String userEmail) throws AccessDeniedException {
        Comment comment = findCommentAndCheckOwnership(commentId, userEmail);
        commentRepository.delete(comment);
    }

    private Comment findCommentAndCheckOwnership(Long commentId, String userEmail) throws AccessDeniedException {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("해당 댓글에 대한 권한이 없습니다.");
        }
        return comment;
    }
}