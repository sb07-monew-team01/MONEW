package com.codeit.monew.domain.article.repository;

import com.codeit.monew.domain.article.dto.request.ArticleSearchCondition;
import com.codeit.monew.domain.article.dto.request.ArticleSearchRequest;
import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.entity.ArticleSource;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface ArticleRepositoryCustom {
    Page<Article> findByKeywordAndSource(ArticleSearchCondition searchCondition);

    long countTotalElements(ArticleSearchCondition searchCondition);
}