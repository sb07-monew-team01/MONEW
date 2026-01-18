package com.codeit.monew.domain.articleView.service;

import com.codeit.monew.domain.articleView.dto.response.ArticleViewDto;

import java.util.UUID;

public interface ArticleViewService {

    ArticleViewDto createArticleView(UUID articleId, UUID userId);
}
