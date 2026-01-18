package com.codeit.monew.domain.notification.dto.request;

import com.codeit.monew.domain.article.dto.request.ArticleSearchCondition;
import com.codeit.monew.domain.article.dto.request.ArticleSearchRequest;
import com.codeit.monew.domain.article.entity.ArticleSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record NotificationPageRequest(
    String cursor,
    LocalDateTime after,
    int limit,
    UUID userid
) {

}
