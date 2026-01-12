package com.codeit.monew.domain.article.dto.request;

import com.codeit.monew.domain.article.entity.ArticleSource;

import java.time.LocalDateTime;
import java.util.List;

public record ArticleSearchRequest(
        String keyword,
        List<ArticleSource> sourceIn,
        LocalDateTime publishDateFrom,
        LocalDateTime publishDateTo,
        String orderBy,
        String direction,
        Object cursor,
        LocalDateTime after,
        Integer limit
) {
}
