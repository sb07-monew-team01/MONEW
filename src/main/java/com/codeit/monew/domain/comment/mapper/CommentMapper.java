package com.codeit.monew.domain.comment.mapper;

import com.codeit.monew.domain.comment.dto.response.CommentDto;
import com.codeit.monew.domain.comment.entity.Comment;

public class CommentMapper {
    private CommentMapper(){}
    public static CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getArticle().getId(),
                comment.getUser().getId(),
                comment.getUser().getNickname(),
                comment.getContent(),
                0L,
                false,
                comment.getCreatedAt()
            );
        }

        public static CommentDto toDto(
                Comment comment,
                long likeCount,
                boolean likedByMe
        ) {
            return new CommentDto(
                    comment.getId(),
                    comment.getArticle().getId(),
                    comment.getUser().getId(),
                    comment.getUser().getNickname(),
                    comment.getContent(),
                    likeCount,
                    likedByMe,
                    comment.getCreatedAt()
            );
        }
    }