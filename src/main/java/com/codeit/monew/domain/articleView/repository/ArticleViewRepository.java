package com.codeit.monew.domain.articleView.repository;

import com.codeit.monew.domain.articleView.entity.ArticleView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ArticleViewRepository extends JpaRepository<ArticleView, UUID> {

    Boolean existsByUserIdAndArticleId(UUID userId, UUID articleId);

    @Query("select av.article.id from ArticleView av where av.user.id = :userId and av.article.id IN :article_id ")
    Set<UUID> findViewedByUserIdAndArticleId(@Param("user_id") UUID userId,
                                             @Param("article_ids") List<UUID> articleIds);
}
