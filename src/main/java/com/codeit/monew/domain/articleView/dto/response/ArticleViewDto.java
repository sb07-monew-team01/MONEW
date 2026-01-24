package com.codeit.monew.domain.articleView.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ArticleViewDto(
        UUID id,
        UUID viewedBy,
        LocalDateTime createdAt,
        UUID articleId,
        String source,
        String sourceUrl,
        String articleTitle,
        LocalDateTime articlePublishedDate,
        String articleSummary,
        Long articleCommentCount,
        Long articleViewCount
) {
}
