package com.codeit.monew.domain.article.repository;

import com.codeit.monew.domain.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID>, ArticleRepositoryCustom {

    // Article 객체 대신 String(URL)만 가져오도록 최적화
    @Query("SELECT a.sourceUrl FROM Article a WHERE a.sourceUrl IN :sourceUrls")
    List<String> findExistingUrlsIn(@Param("sourceUrls") List<String> sourceUrls);
}
