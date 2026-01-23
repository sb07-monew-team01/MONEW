package com.codeit.monew.domain.commentuserlike.repository;

import com.codeit.monew.domain.commentuserlike.entity.CommentUserLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CommentUserLikeRepository extends JpaRepository<CommentUserLike, UUID> {

    @Query("""
        select cul
        from CommentUserLike cul
        where cul.user.id = :userId
          and cul.comment.id = :commentId
    """)
    Optional<CommentUserLike> findByUserIdAndCommentId(
            @Param("userId") UUID userId,
            @Param("commentId") UUID commentId
    );

    @Query("""
        select count(cul)
        from CommentUserLike cul
        where cul.comment.id = :commentId
    """)
    Long countByCommentId(@Param("commentId") UUID commentId);

    @Query("""
        select (count(cul) > 0)
        from CommentUserLike cul
        where cul.user.id = :userId
          and cul.comment.id = :commentId
    """)
    boolean existsByUserIdAndCommentId(
            @Param("userId") UUID userId,
            @Param("commentId") UUID commentId
    );

    @Modifying
    @Query("""
        delete from CommentUserLike cul
        where cul.user.id = :userId
          and cul.comment.id = :commentId
    """)
    void deleteByUserIdAndCommentId(
            @Param("userId") UUID userId,
            @Param("commentId") UUID commentId
    );
}
