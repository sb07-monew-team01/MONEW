package com.codeit.monew.domain.comment.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record CommentPageResponse(
    List<CommentDto> content,
    String nextCursor,
    LocalDateTime nextAfter,
    int size,
    Long totalElements,
    boolean hasNext
){
}
