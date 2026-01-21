package com.codeit.monew.domain.comment.repository;

import com.codeit.monew.domain.comment.dto.request.CommentOrderBy;
import com.codeit.monew.domain.comment.dto.response.CommentWithLikeCount;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CommentRepositoryCustom {
    Slice<CommentWithLikeCount> findByArticleIdOrderBy(
            UUID articleId,
            CommentOrderBy orderBy,
            String cursor,
            int limit
    );
}
