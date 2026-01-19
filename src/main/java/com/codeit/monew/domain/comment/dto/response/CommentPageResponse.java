package com.codeit.monew.domain.comment.dto.response;

import java.util.List;

public record CommentPageResponse(
    List<CommentDto> contents,
    String nextCursor,
    String nextAfter,
    int size,
    long totalElements,
    boolean hasNext
){
}
