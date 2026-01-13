package com.codeit.monew.domain.article.service;

import com.codeit.monew.common.dto.PageResponse;
import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.dto.request.ArticleSearchRequest;
import com.codeit.monew.domain.article.dto.response.ArticleDto;
import com.codeit.monew.domain.interest.entity.Interest;

import java.util.List;

public interface ArticleService {
    PageResponse<ArticleDto> searchByKeyword(ArticleSearchRequest searchRequest);

    void createArticle(ArticleCreateRequest request, List<Interest>  interests);
}
