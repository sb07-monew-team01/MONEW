package com.codeit.monew.domain.comment.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentSearchRequest(
        UUID articleId,
        UUID userId,
        CommentOrderBy orderBy,
        SortDirection direction,
        String cursor,
        LocalDateTime after,
        int limit
) {
}
