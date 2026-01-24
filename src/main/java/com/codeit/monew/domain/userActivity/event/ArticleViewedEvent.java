package com.codeit.monew.domain.userActivity.event;

import com.codeit.monew.domain.articleView.entity.ArticleView;

import java.util.UUID;

public record ArticleViewedEvent(
        UUID userId,
        ArticleView articleView
) {
}
