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
                null, // 아직 좋아요 기능 구현 안 됨
                null, // 아직 좋아요 기능 구현 안 됨
                comment.getCreatedAt()
            );
        }
    }

