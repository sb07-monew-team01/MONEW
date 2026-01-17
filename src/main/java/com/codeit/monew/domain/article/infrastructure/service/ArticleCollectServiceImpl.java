package com.codeit.monew.domain.article.infrastructure.service;

import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.infrastructure.ArticleCollector;
import com.codeit.monew.domain.article.infrastructure.CollectedArticleMapper;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interest.repository.InterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleCollectServiceImpl implements ArticleCollectService {

    private final List<ArticleCollector> articleCollectors;
    private final ArticleRepository articleRepository;
    private final InterestRepository interestRepository;
    private final CollectedArticleMapper collectedArticleMapper;

    @Override
    public void collectAndSave() {
        List<Interest> interests = interestRepository.findAll();

        List<ArticleCreateRequest> collectedArticles = new ArrayList<>();
        for (ArticleCollector articleCollector : articleCollectors) {
            collectedArticles.addAll(articleCollector.collect(interests));
        }

        List<String> collectedUrls = collectedArticles.stream()
                .map(ArticleCreateRequest::sourceUrl)
                .toList();

        List<String> existingUrls = articleRepository.findAllBySourceUrlIn(collectedUrls).stream()

                .map(Article::getSourceUrl)
                .toList();

        List<Article> newArticles = collectedArticles.stream()
                .filter(request -> !existingUrls.contains(request.sourceUrl()))
                .map(collectedArticleMapper::toEntity)
                .toList();


        articleRepository.saveAll(newArticles);

    }
}
