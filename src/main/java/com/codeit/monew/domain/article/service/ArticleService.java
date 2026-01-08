package com.codeit.monew.domain.article.service;

import com.codeit.monew.domain.article.entity.Article;

import java.util.List;

public interface ArticleService {
    List<Article> searchByKeyword(String keyword);
}
