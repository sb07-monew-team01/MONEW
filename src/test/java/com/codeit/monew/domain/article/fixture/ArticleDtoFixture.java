package com.codeit.monew.domain.article.fixture;

import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.dto.response.ArticleDto;
import com.codeit.monew.domain.article.entity.Article;

public class ArticleDtoFixture {
    public static ArticleDto createDtoEntity(Article article) {
        return new ArticleDto(
                article.getId(),
                article.getSource().toString(),
                article.getSourceUrl(),
                article.getTitle(),
                article.getPublishDate(),
                article.getSummary(),
                article.getCommentCount(),
                article.getViewCount(),
                true
        );
    }
}
