package com.codeit.monew.domain.comment.repository;

import com.codeit.monew.domain.comment.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;import org.springframework.data.domain.Page;


public interface CommentRepository extends JpaRepository<Comment, UUID> {

    Page<Comment> findByArticleId(UUID articleId, Pageable pageable);
}
