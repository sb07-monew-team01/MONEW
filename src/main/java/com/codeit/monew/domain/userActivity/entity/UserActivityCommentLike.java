package com.codeit.monew.domain.userActivity.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserActivityCommentLike(
        UUID id,
        LocalDateTime createdAt,
        UUID commentId,
        UUID articleId,
        String articleTitle,
        UUID commentUserId,
        String commentUserNickname,
        String commentContent,
        Long commentLikeCount,
        LocalDateTime commentCreatedAt
){}