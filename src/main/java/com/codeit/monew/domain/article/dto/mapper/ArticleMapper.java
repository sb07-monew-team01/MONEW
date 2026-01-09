package com.codeit.monew.domain.article.dto.mapper;

import com.codeit.monew.domain.article.dto.response.ArticleDto;
import com.codeit.monew.domain.article.entity.Article;
import org.springframework.stereotype.Component;

@Component
public class ArticleMapper {
    public ArticleDto toDto(Article article) {
        return new ArticleDto(
                article.getTitle(),
                article.getSummary(),
                article.getSource().toString()
        );
    }
}
