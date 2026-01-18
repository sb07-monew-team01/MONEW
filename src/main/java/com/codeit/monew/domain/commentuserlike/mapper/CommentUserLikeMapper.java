package com.codeit.monew.domain.commentuserlike.mapper;

import com.codeit.monew.domain.commentuserlike.dto.CommentUserLikeDto;
import com.codeit.monew.domain.commentuserlike.entity.CommentUserLike;

public class CommentUserLikeMapper {
    private CommentUserLikeMapper() {}
    public static CommentUserLikeDto toDto(
            CommentUserLike like,
            long commentLikeCount
    ) {
        return new CommentUserLikeDto(
                like.getId(),
                like.getUser().getId(),
                like.getCreatedAt(),
                like.getComment().getId(),
                like.getComment().getArticle().getId(),
                like.getComment().getUser().getId(),
                like.getComment().getUser().getNickname(),
                like.getComment().getContent(),
                commentLikeCount,
                like.getComment().getCreatedAt()
        );
    }
}

