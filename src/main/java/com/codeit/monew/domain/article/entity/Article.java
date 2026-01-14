package com.codeit.monew.domain.article.entity;

import com.codeit.monew.domain.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Table(name = "articles")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Article extends BaseUpdatableEntity {

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

    @Column(name = "view_count", nullable = false)
    @ColumnDefault("0")
    private long viewCount;

    @Column(name = "comment_count", nullable = false)
    @ColumnDefault("0")
    private long commentCount;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 테스트용
    @Builder
    public Article(ArticleSource source, String sourceUrl, String title, LocalDateTime publishDate, String summary, LocalDateTime deletedAt, long viewCount, long commentCount) {
        this.source = source;
        this.sourceUrl = sourceUrl;
        this.title = title;
        this.publishDate = publishDate;
        this.summary = summary;
        this.deletedAt = deletedAt;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
    }
}
