package com.codeit.monew.article.fixture;

import com.codeit.monew.domain.article.dto.request.ArticleSearchRequest;

import java.util.UUID;

public class ArticleSearchRequestFixture {
    public static ArticleSearchRequest createWithOrderBy(String orderBy, int size) {
        return ArticleSearchRequest.builder()
                .orderBy(orderBy)
                .direction("DESC")
                .limit(size)
                .build();
    }

    public static ArticleSearchRequest createWithInterestId(UUID interestId) {
        return ArticleSearchRequest.builder()
                .interestId(interestId)
                .build();
    }
}
