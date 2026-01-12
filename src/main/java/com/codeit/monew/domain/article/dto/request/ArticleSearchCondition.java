package com.codeit.monew.domain.article.dto.request;

import com.codeit.monew.domain.article.entity.ArticleSource;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
@Builder
public record ArticleSearchCondition(
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
