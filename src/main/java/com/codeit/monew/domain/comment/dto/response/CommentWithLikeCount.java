package com.codeit.monew.domain.comment.dto.response;

import com.codeit.monew.domain.comment.entity.Comment;

public record CommentWithLikeCount(
    Comment comment,
    long likeCount
){
}
