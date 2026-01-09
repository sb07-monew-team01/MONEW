package com.codeit.monew.domain.article.repository;

import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.entity.ArticleSource;

import java.util.List;

public interface ArticleRepositoryCustom {
    List<Article> findByKeywordAndSource(String keyword, List<ArticleSource> sources);
}