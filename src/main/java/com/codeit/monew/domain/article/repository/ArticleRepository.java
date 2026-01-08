package com.codeit.monew.domain.article.repository;

import com.codeit.monew.domain.article.entity.Article;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID> {

    @Query("SELECT a FROM Article a " +
            "WHERE a.title LIKE %:keyword% OR a.summary Like %:keyword%")
    List<Article> findByKeyword(@Param("keyword") String keyword);
}
