package com.codeit.monew.domain.article.repository;

import com.codeit.monew.domain.article.dto.request.ArticleSearchRequest;
import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.entity.ArticleSource;

import java.time.LocalDateTime;
import java.util.List;

public interface ArticleRepositoryCustom {
    List<Article> findByKeywordAndSource(ArticleSearchRequest searchRequest);
}