package com.codeit.monew.domain.article.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ArticleOrderBy {
    PUBLISH_DATE("publishDate"),
    COMMENT_COUNT("commentCount"),
    VIEW_COUNT("viewCount");

    private final String description;
}
