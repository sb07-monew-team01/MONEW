package com.codeit.monew.domain.userActivity.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserActivityArticleView(
        UUID id,
        UUID viewedBy,
        LocalDateTime createdAt,
        UUID articleId,
        com.codeit.monew.domain.article.entity.ArticleSource source,
        String sourceUrl,
        String articleTitle,
        LocalDateTime articlePublishedDate,
        String articleSummary,
        Long articleCommentCount,
        Long articleViewCount
){
}
