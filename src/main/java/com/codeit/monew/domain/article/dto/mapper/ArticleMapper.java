package com.codeit.monew.domain.article.dto.mapper;

import com.codeit.monew.domain.article.dto.response.ArticleDto;
import com.codeit.monew.domain.article.entity.Article;
import org.springframework.stereotype.Component;

@Component
public class ArticleMapper {
    public ArticleDto toDto(Article article, boolean viewedByMe) {
        return new ArticleDto(
                article.getId(),
                article.getSource().toString(),
                article.getSourceUrl(),
                article.getTitle(),
                article.getPublishDate(),
                article.getSummary(),
                article.getCommentCount(),
                article.getViewCount(),
                viewedByMe
        );
    }
}
