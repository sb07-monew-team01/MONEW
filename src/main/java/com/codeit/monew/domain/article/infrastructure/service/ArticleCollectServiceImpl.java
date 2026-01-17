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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleCollectServiceImpl implements ArticleCollectService {

    private final List<ArticleCollector> articleCollectors;
    private final ArticleRepository articleRepository;
    private final InterestRepository interestRepository;
    private final CollectedArticleMapper collectedArticleMapper;

    @Transactional
    @Override
    public void collectAndSave() {
        List<Interest> interests = interestRepository.findAll();

        // 관심사가 없다면 종료
        if(interests.isEmpty()){
            return;
        }

        // 수집된 기사들 중,
        List<ArticleCreateRequest> collectedArticles =
                articleCollectors.stream()
                        .flatMap(c -> c.collect(interests).stream())
                        .collect(Collectors.toMap(
                                ArticleCreateRequest::sourceUrl,
                                request -> request,
                                (oldRequest, newRequest) -> oldRequest
                        ))
                        .values().stream()
                        .toList();

        List<String> collectedUrls = collectedArticles.stream()
                .map(ArticleCreateRequest::sourceUrl)
                .toList();

        // 수집된 기사가 없다면 종료
        if(collectedUrls.isEmpty()){
            return;
        }

        Set<String> existingUrlSet = articleRepository.findAllBySourceUrlIn(collectedUrls).stream()
                .map(Article::getSourceUrl)
                .collect(Collectors.toSet());

        List<Article> newArticles = collectedArticles.stream()
                .filter(request -> !existingUrlSet.contains(request.sourceUrl()))
                .map(collectedArticleMapper::toEntity)
                .toList();

        articleRepository.saveAll(newArticles);

    }
}
