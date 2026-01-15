package com.codeit.monew.domain.comment.dto.response;

import com.codeit.monew.domain.comment.entity.Comment;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentDto(
        UUID id,
        UUID articleId,
        UUID userId,
        String userNickname,
        String content,
        Long likeCount,
        Boolean likedByMe,
        LocalDateTime createdAt
) {
    public static CommentDto from(Comment comment)
    {
        return new CommentDto(
                comment.getId(),
                comment.getArticle().getId(),
                comment.getUser().getId(),
                comment.getUser().getNickname(),
                comment.getContent(),
                null,
                null,
                comment.getCreatedAt()
        );
    }
}
