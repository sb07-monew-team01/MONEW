package com.codeit.monew.domain.article.repository;

import com.codeit.monew.domain.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID>, ArticleRepositoryCustom {

    // Article 객체 대신 String(URL)만 가져오도록 최적화
    @Query("SELECT a.sourceUrl FROM Article a WHERE a.sourceUrl IN :sourceUrls")
    List<String> findExistingUrlsIn(@Param("sourceUrls") List<String> sourceUrls);

    List<Article> findByPublishDateGreaterThanEqualAndPublishDateLessThan(
            LocalDateTime start, LocalDateTime end);

    default List<Article> findByPublishDate(LocalDate date) {
        return findByPublishDateGreaterThanEqualAndPublishDateLessThan(
                date.atStartOfDay(), date.plusDays(1).atStartOfDay());
    }

    @Query("SELECT DISTINCT a.sourceUrl FROM Article a WHERE a.publishDate >= ?1 AND a.publishDate < ?2")
    Set<String> findSourceUrlsByPublishDateBetween(LocalDateTime from, LocalDateTime to);
}