package com.codeit.monew.domain.comment.repository;

import com.codeit.monew.domain.comment.dto.request.CommentOrderBy;
import com.codeit.monew.domain.comment.dto.request.CommentWithLikeCount;
import com.codeit.monew.domain.comment.dto.request.SortDirection;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CommentRepositoryCustom {
    Slice<CommentWithLikeCount> findByArticleIdOrderBy(
            UUID articleId,
            CommentOrderBy orderBy,
            SortDirection direction,
            String cursor,
            LocalDateTime createdAt,
            int limit
    );
}
