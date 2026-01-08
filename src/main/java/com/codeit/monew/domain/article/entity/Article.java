package com.codeit.monew.domain.article.entity;

import com.codeit.monew.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "articles")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Article extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private ArticleSource source;

    @Column(name = "source_url", nullable = false)
    private String sourceUrl;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "publish_date", nullable = false)
    private LocalDateTime publishDate;

    @Column(name = "summary", nullable = false)
    private String summary;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 테스트용
    @Builder
    public Article(ArticleSource source, String sourceUrl, String title, LocalDateTime publishDate, String summary, LocalDateTime deletedAt) {
        this.source = source;
        this.sourceUrl = sourceUrl;
        this.title = title;
        this.publishDate = publishDate;
        this.summary = summary;
        this.deletedAt = deletedAt;
    }
}
