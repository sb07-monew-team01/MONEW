package com.codeit.monew.article.fixture;

import com.codeit.monew.domain.article.dto.request.ArticleSearchRequest;

public class ArticleSearchRequestFixture {
    public static ArticleSearchRequest createWithOrderBy(String orderBy) {
        return ArticleSearchRequest.builder()
                .orderBy(orderBy)
                .direction("DESC")
                .limit(10)
                .build();
    }
}
