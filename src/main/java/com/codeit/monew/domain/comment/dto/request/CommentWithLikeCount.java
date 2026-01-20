package com.codeit.monew.domain.comment.dto.request;

import com.codeit.monew.domain.comment.entity.Comment;

public record CommentWithLikeCount(
    Comment comment,
    Long likeCount
){
}
