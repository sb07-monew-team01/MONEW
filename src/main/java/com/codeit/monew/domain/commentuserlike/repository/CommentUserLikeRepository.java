package com.codeit.monew.domain.commentuserlike.repository;


import com.codeit.monew.domain.commentuserlike.entity.CommentUserLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CommentUserLikeRepository extends JpaRepository<CommentUserLike, UUID> {
    Optional<CommentUserLike> findByUserIdAndCommentId(UUID userId, UUID commentId); // 좋아요 취소나 조회
    Long countByCommentId(UUID commentId); // 좋아요 수
    boolean existsByUserIdAndCommentId(UUID userId, UUID commentId); // 좋아요 눌렀는지 확인
}
