package com.codeit.monew.domain.articleView.entity;

import com.codeit.monew.domain.BaseEntity;
import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "article_views",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_article_views",
                columnNames = {"user_id", "article_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ArticleView extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    public ArticleView(User user, Article article) {
        this.user = user;
        this.article = article;
    }
}
