package com.codeit.monew.article.fixture;

import com.codeit.monew.domain.article.dto.request.ArticleSearchRequest;

public class ArticleSearchRequestFixture {
    public static ArticleSearchRequest createDefault() {
        return ArticleSearchRequest.builder().build();
    }
}
