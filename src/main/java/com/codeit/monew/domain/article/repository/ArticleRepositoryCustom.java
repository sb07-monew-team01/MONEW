package com.codeit.monew.domain.article.repository;

import com.codeit.monew.domain.article.dto.request.ArticleSearchCondition;
import com.codeit.monew.domain.article.entity.Article;
import org.springframework.data.domain.Slice;

public interface ArticleRepositoryCustom {
    Slice<Article> findByKeywordAndSource(ArticleSearchCondition searchCondition);

    long countTotalElements(ArticleSearchCondition searchCondition);
}