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

import java.util.HashSet;
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

        // 수집된 기사들 중, 중복이 있다면, 중복을 제거하고 리스트로 생성
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

        // 1000개씩 끊어서 존재하는 데이터인지 판단.
        // 존재하는 데이터라면, existingUrlSet 추가
        Set<String> existingUrlSet = new HashSet<>();
        int batchSize = 1000;
        for (int i = 0; i < collectedUrls.size(); i += batchSize) {
            List<String> batch = collectedUrls.subList(i, Math.min(i + batchSize, collectedUrls.size()));
            existingUrlSet.addAll(articleRepository.findExistingUrlsIn(batch));
        }

        // ExsistingUrlSet과 collectArtilce를 비교하여, 존재하지 않는 URL만 리스트화 -> newArticles
        List<Article> newArticles = collectedArticles.stream()
                .filter(request -> !existingUrlSet.contains(request.sourceUrl()))
                .map(collectedArticleMapper::toEntity)
                .toList();


        // saveAll(10만건)을 호출해도, 내부 설정에 따라 JPA가 알아서 1,000개 단위로 쪼개서 DB에 Insert 명령을 보냄
        articleRepository.saveAll(newArticles);

    }
}
