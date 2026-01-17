package com.codeit.monew.domain.article.infrastructure.service;

import com.codeit.monew.domain.article.dto.mapper.ArticleMapper;
import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.infrastructure.ArticleCollector;
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
    private final ArticleMapper articleMapper;

    @Override
    public void collectAndSave() {
        List<Interest> interests = interestRepository.findAll();
        if (interests.isEmpty()) {
            return;
        }

        List<ArticleCreateRequest> articleCreateRequests = new ArrayList<>();
        for(ArticleCollector articleCollector : articleCollectors) {
            List<ArticleCreateRequest> articles = articleCollector.collect(interests);
        }

    }
}
