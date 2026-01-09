package com.codeit.monew.domain.article.service;

import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.dto.response.ArticleDto;

import java.util.List;
import java.util.UUID;

public interface ArticleService {
    List<ArticleDto> searchByKeyword(String keyword);

    void createArticle(ArticleCreateRequest request, UUID interestId);
}
