package com.codeit.monew.domain.article.repository;

import com.codeit.monew.domain.article.dto.request.ArticleSearchCondition;
import com.codeit.monew.domain.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID>, ArticleRepositoryCustom {

    boolean existsBySourceUrl(String sourceUrl);
}
