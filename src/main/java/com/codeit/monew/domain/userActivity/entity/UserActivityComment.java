package com.codeit.monew.domain.userActivity.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserActivityComment(
        UUID id,
        UUID articleId,
        String articleTitle,
        UUID userId,
        String userNickname,
        String content,
        Long likeCount,
        LocalDateTime createdAt
){}