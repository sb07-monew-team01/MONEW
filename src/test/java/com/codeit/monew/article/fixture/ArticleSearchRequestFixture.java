package com.codeit.monew.article.fixture;

import com.codeit.monew.domain.article.dto.request.ArticleSearchRequest;

public class ArticleSearchRequestFixture {
    public static ArticleSearchRequest createWithOrderBy(String orderBy, int size) {
        return ArticleSearchRequest.builder()
                .orderBy(orderBy)
                .direction("DESC")
                .limit(size)
                .build();
    }
}
