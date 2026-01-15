package com.codeit.monew.domain.article.dto.request;

import com.codeit.monew.domain.article.entity.ArticleSource;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;
@Builder
public record ArticleSearchCondition(
        List<String> keywords,
        List<ArticleSource> sourceIn,
        LocalDateTime publishDateFrom,
        LocalDateTime publishDateTo,
        String orderBy,
        String direction,
        String cursor,
        LocalDateTime after,
        Integer limit
) {

    public static ArticleSearchCondition of(ArticleSearchRequest request, List<String> keywords) {
        return new ArticleSearchCondition(
                keywords,
                request.sourceIn(),
                request.publishDateFrom(),
                request.publishDateTo(),
                request.orderBy(),
                request.direction(),
                request.cursor(),
                request.after(),
                request.limit()
        );
    }
}
