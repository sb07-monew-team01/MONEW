package com.codeit.monew.domain.userActivity.event.dto;

import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.comment.entity.Comment;

import java.util.UUID;

public record CommentCreatedEvent(
        UUID userId,
        Comment comment,
        Article article,
        Long likeCount
) {
}
