package com.codeit.monew.domain.article.service;

import com.codeit.monew.domain.article.dto.mapper.ArticleMapper;
import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.dto.response.ArticleDto;
import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private ArticleRepository articleRepository;
    private ArticleMapper articleMapper;

    @Transactional
    @Override
    public List<ArticleDto> searchByKeyword(String keyword) {
        List<Article> articles = articleRepository.findByKeyword(keyword);
        return articles.stream()
                .map(articleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void createArticle(ArticleCreateRequest request, UUID interestId) {
        if(articleRepository.existsBySourceUrl(request.sourceUrl()))
            return;

        Article article = Article.builder()
                .source(request.source())
                .sourceUrl(request.sourceUrl())
                .title(request.title())
                .publishDate(request.publishDate())
                .summary(request.summary())
                .build();

        articleRepository.save(article);
    }
}
