package com.codeit.monew.domain.article.service;

import com.codeit.monew.domain.article.dto.request.ArticleSearchRequest;
import com.codeit.monew.domain.article.dto.response.ArticleDto;
import com.codeit.monew.global.dto.PageResponse;

import java.util.UUID;

public interface ArticleService {
    PageResponse<ArticleDto> searchByKeyword(ArticleSearchRequest searchRequest, UUID userId);

    ArticleDto searchByUserIdAndArticleId(UUID userId, UUID articleId);

    void softDelete(UUID articleId);

    void hardDelete(UUID articleId);
}
