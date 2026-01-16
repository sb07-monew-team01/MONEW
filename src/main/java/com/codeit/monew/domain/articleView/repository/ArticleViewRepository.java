package com.codeit.monew.domain.articleView.repository;

import com.codeit.monew.domain.articleView.entity.ArticleView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ArticleViewRepository extends JpaRepository<ArticleView, UUID> {

    Boolean existsByUserIdAndArticleId(UUID userId, UUID articleId);
}
