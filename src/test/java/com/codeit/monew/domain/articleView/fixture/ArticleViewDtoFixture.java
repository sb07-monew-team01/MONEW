package com.codeit.monew.domain.articleView.fixture;

import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.articleView.dto.response.ArticleViewDto;
import com.codeit.monew.domain.articleView.entity.ArticleView;
import com.codeit.monew.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

public class ArticleViewDtoFixture {

    public static ArticleViewDto createDtoEntity(ArticleView view) {

        return new ArticleViewDto(
                view.getId(),
                view.getUser().getId(),
                LocalDateTime.now(),
                view.getArticle().getId(),
                view.getArticle().getSource().toString(),
                view.getArticle().getSourceUrl(),
                view.getArticle().getTitle(),
                view.getArticle().getPublishDate(),
                view.getArticle().getSummary(),
                view.getArticle().getCommentCount(),
                view.getArticle().getViewCount()
        );
    }
}