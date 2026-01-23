package com.codeit.monew.domain.commentuserlike.repository;

import com.codeit.monew.domain.comment.dto.response.CommentWithLikeCount;

import java.util.List;
import java.util.UUID;

public interface CommentUserLikeQueryRepository {
    List<CommentWithLikeCount> findCommentsOrderByLikeCount(
            UUID articleId,
            int limit
    );
}

