package com.codeit.monew.domain.articleView.dto.mapper;

import com.codeit.monew.domain.articleView.dto.response.ArticleViewDto;
import com.codeit.monew.domain.articleView.entity.ArticleView;
import org.springframework.stereotype.Component;

@Component
public class ArticleViewMapper {
    public ArticleViewDto toDto(ArticleView articleView) {
        return new ArticleViewDto(
                articleView.getId(),
                articleView.getUser().getId(),
                articleView.getCreatedAt(),
                articleView.getArticle().getId(),
                articleView.getArticle().getSource().toString(),
                articleView.getArticle().getSourceUrl(),
                articleView.getArticle().getTitle(),
                articleView.getArticle().getPublishDate(),
                articleView.getArticle().getSummary(),
                articleView.getArticle().getCommentCount(),
                articleView.getArticle().getViewCount()
        );
    }
}
