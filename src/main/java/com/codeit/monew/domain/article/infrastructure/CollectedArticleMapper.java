package com.codeit.monew.domain.article.infrastructure;

import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.entity.Article;
import org.springframework.stereotype.Component;

@Component
public class CollectedArticleMapper {
    public Article toEntity(ArticleCreateRequest request) {
        return new Article(
                request.source(),
                request.sourceUrl(),
                request.title(),
                request.publishDate(),
                request.summary(),
                null
        );
    }

}
