package com.codeit.monew.domain.article.dto.request;

import com.codeit.monew.domain.article.entity.ArticleSource;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ArticleSearchRequest(
        String keyword,
        List<ArticleSource> sourceIn,
        LocalDateTime publishDateFrom,
        LocalDateTime publishDateTo,
        String orderBy,
        String direction,
        String cursor,
        LocalDateTime after,
        Integer limit
) {
    public ArticleSearchRequest {
        if (orderBy == null) orderBy = "publishDate";
        if (direction == null) direction = "DESC";
        if (limit == null) limit = 10;
    }
}
