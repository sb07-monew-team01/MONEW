package com.codeit.monew.domain.article.fixture;

import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.entity.ArticleSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

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

    public static Article createWithViewAndComment(ArticleCreateRequest request, long view, long comment) {
        Article article = Article.builder()
                .source(request.source())
                .sourceUrl(request.sourceUrl())
                .title(request.title())
                .publishDate(request.publishDate())
                .summary(request.summary())
                .build();
        ReflectionTestUtils.setField(article, "viewCount", view);
        ReflectionTestUtils.setField(article, "commentCount", comment);
        return article;
    }

    public static Article createEntityWithSourceUrl(String sourceUrl) {
        return  Article.builder()
                .source(ArticleSource.NAVER)
                .sourceUrl(sourceUrl)
                .title("아무 제목")
                .publishDate(LocalDateTime.now())
                .summary("아무 요약")
                .build();
    }
}
