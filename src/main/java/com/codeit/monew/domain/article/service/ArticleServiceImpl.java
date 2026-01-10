package com.codeit.monew.domain.article.service;

import com.codeit.monew.domain.article.dto.mapper.ArticleMapper;
import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.dto.request.ArticleSearchCondition;
import com.codeit.monew.domain.article.dto.request.ArticleSearchRequest;
import com.codeit.monew.domain.article.dto.response.ArticleDto;
import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.entity.ArticleSource;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import com.codeit.monew.domain.interest.entity.Interest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private ArticleRepository articleRepository;
    private ArticleMapper articleMapper;

    @Transactional(readOnly = true)
    @Override
    public List<ArticleDto> searchByKeyword(ArticleSearchRequest searchRequest) {

        ArticleSearchCondition condition = new ArticleSearchCondition(
                searchRequest.keyword(),
                searchRequest.sourceIn(),
                searchRequest.publishDateFrom(),
                searchRequest.publishDateTo()
        );

        List<Article> articles = articleRepository.findByKeywordAndSource(condition);

        return articles.stream()
                .map(articleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void createArticle(ArticleCreateRequest request, List<Interest> interests) {
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
