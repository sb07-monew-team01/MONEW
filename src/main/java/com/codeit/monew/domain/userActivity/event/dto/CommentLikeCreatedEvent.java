package com.codeit.monew.domain.userActivity.event.dto;

import com.codeit.monew.domain.commentuserlike.entity.CommentUserLike;

import java.util.UUID;

public record CommentLikeCreatedEvent(
        UUID userId,
        CommentUserLike commentLike,
        Long likeCount
) {
}
