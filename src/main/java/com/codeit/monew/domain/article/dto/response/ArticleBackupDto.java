package com.codeit.monew.domain.article.dto.response;

import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.entity.ArticleSource;

import java.time.LocalDateTime;

public record ArticleBackupDto(
        String source,
        String sourceUrl,
        String title,
        String summary,
        LocalDateTime publishDate
) {
    public static ArticleBackupDto fromEntity(Article article) {
        return new ArticleBackupDto(
                article.getSource().toString(),
                article.getSourceUrl(),
                article.getTitle(),
                article.getSummary(),
                article.getPublishDate()
        );
    }

    public Article toEntity() {
        return Article.builder()
                .source(ArticleSource.valueOf(this.source()))
                .sourceUrl(this.sourceUrl())
                .title(this.title())
                .summary(this.summary())
                .publishDate(this.publishDate())
                .build();
    }
}
