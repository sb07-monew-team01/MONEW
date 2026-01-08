package com.codeit.monew.domain.article.service;

import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService{

    private ArticleRepository articleRepository;

    @Override
    public List<Article> searchByKeyword(String keyword) {
        return articleRepository.findByTitleContaining(keyword);
    }
}
