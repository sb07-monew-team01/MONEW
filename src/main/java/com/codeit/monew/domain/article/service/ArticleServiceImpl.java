package com.codeit.monew.domain.article.service;

import com.codeit.monew.domain.article.dto.mapper.ArticleMapper;
import com.codeit.monew.domain.article.dto.response.ArticleDto;
import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService{

    private ArticleRepository articleRepository;
    private ArticleMapper articleMapper;

    @Override
    public List<ArticleDto> searchByKeyword(String keyword) {
        List<Article> articles = articleRepository.findByKeyword(keyword);
        return articles.stream()
                .map(article -> articleMapper.toDto(article))
                .collect(Collectors.toList());
    }
}
