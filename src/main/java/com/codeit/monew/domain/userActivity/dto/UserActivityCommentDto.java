package com.codeit.monew.domain.userActivity.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserActivityCommentDto(
        UUID id,
        UUID articleId,
        String articleTitle,
        UUID userId,
        String userNickname,
        String content,
        Long likeCount,
        LocalDateTime createdAt
){}