package com.codeit.monew.domain.article.service;

import com.codeit.monew.domain.article.dto.response.ArticleDto;
import com.codeit.monew.domain.article.entity.Article;

import java.util.List;

public interface ArticleService {
    List<ArticleDto> searchByKeyword(String keyword);
}
