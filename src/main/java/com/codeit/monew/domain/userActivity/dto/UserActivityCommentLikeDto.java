package com.codeit.monew.domain.userActivity.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserActivityCommentLikeDto(
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