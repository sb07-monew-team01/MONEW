package com.codeit.monew.domain.article.service;

import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.dto.request.ArticleSearchRequest;
import com.codeit.monew.domain.article.dto.response.ArticleDto;
import com.codeit.monew.domain.article.entity.ArticleSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ArticleService {
    List<ArticleDto> searchByKeyword(ArticleSearchRequest searchRequest);

    void createArticle(ArticleCreateRequest request, UUID interestId);
}
