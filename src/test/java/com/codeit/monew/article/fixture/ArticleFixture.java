package com.codeit.monew.article.fixture;

import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.entity.Article;

public class ArticleFixture {
    public static Article createEntity(ArticleCreateRequest request) {
        return Article.builder()
                .source(request.source())
                .sourceUrl(request.sourceUrl())
                .title(request.title())
                .publishDate(request.publishDate())
                .summary(request.summary())
                .build();
    }
}
